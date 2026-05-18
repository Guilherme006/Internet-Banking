package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Boleto;
import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.application.domain.TipoTransacao;
import com.banco.pagamento.application.domain.Transacao;
import com.banco.pagamento.application.domain.exception.BoletoNaoEncontradoException;
import com.banco.pagamento.application.domain.exception.ContaNaoEncontradaException;
import com.banco.pagamento.ports.outbound.BancoCentralPort;
import com.banco.pagamento.ports.outbound.BoletoRepositoryPort;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.IdempotenciaPort;
import com.banco.pagamento.ports.outbound.NotificacaoKafkaPort;
import com.banco.pagamento.ports.outbound.TransacaoRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class ProcessarPagamentoUseCase {

    private final ContaRepositoryPort contaRepository;
    private final BoletoRepositoryPort boletoRepository;
    private final BancoCentralPort bancoCentral;
    private final NotificacaoKafkaPort notificacaoKafka;
    private final IdempotenciaPort idempotencia;
    private final TransacaoRepositoryPort transacaoRepository;

    public ProcessarPagamentoUseCase(
            ContaRepositoryPort contaRepository,
            BoletoRepositoryPort boletoRepository,
            BancoCentralPort bancoCentral,
            NotificacaoKafkaPort notificacaoKafka,
            IdempotenciaPort idempotencia,
            TransacaoRepositoryPort transacaoRepository) {
        this.contaRepository = contaRepository;
        this.boletoRepository = boletoRepository;
        this.bancoCentral = bancoCentral;
        this.notificacaoKafka = notificacaoKafka;
        this.idempotencia = idempotencia;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional
    public PagamentoResultado processar(PagamentoComando comando) {
        Optional<PagamentoResultado> cached = idempotencia.buscar(comando.chaveIdempotencia());
        if (cached.isPresent()) {
            return cached.get().reprocessado()
                ? cached.get()
                : new PagamentoResultado(
                    cached.get().transacaoId(),
                    cached.get().numeroConta(),
                    cached.get().codigoBarra(),
                    cached.get().valorDebitado(),
                    cached.get().dataHora(),
                    cached.get().status(),
                    true
                );
        }

        Conta conta = contaRepository.buscarComLockPessimista(comando.numeroConta())
            .orElseThrow(() -> new ContaNaoEncontradaException(comando.numeroConta()));

        Boleto boleto = boletoRepository.buscarPorCodigo(comando.codigoBarra())
            .orElseThrow(() -> new BoletoNaoEncontradoException(comando.codigoBarra()));

        bancoCentral.validarBoleto(boleto.getCodigoBarra());

        conta.debitar(boleto.getValor());
        boleto.liquidar();

        contaRepository.salvar(conta);
        boletoRepository.salvar(boleto);

        String transacaoId = UUID.randomUUID().toString();
        LocalDateTime agora = LocalDateTime.now();
        PagamentoResultado resultado = new PagamentoResultado(
            transacaoId,
            conta.getNumeroConta(),
            boleto.getCodigoBarra(),
            boleto.getValor(),
            agora,
            "APROVADO",
            false
        );

        Transacao transacao = Transacao.builder()
            .id(transacaoId)
            .numeroConta(conta.getNumeroConta())
            .tipo(TipoTransacao.DEBITO)
            .descricao("Pagamento Boleto - " + boleto.getBeneficiario())
            .valor(boleto.getValor())
            .dataHora(agora)
            .saldoApos(conta.getSaldo())
            .categoria("Boleto")
            .build();
        transacaoRepository.salvar(transacao);

        PagamentoEvento evento = new PagamentoEvento(
            transacaoId,
            "BOLETO_PAGO",
            conta.getNumeroConta(),
            boleto.getCodigoBarra(),
            boleto.getValor(),
            agora
        );
        notificacaoKafka.publicar(evento);

        idempotencia.salvar(comando.chaveIdempotencia(), resultado);

        return resultado;
    }
}
