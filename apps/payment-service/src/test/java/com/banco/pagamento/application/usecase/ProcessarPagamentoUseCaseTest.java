package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Boleto;
import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.application.domain.StatusBoleto;
import com.banco.pagamento.application.domain.TipoTransacao;
import com.banco.pagamento.application.domain.Transacao;
import com.banco.pagamento.application.domain.exception.BoletoJaPagoException;
import com.banco.pagamento.application.domain.exception.BoletoNaoEncontradoException;
import com.banco.pagamento.application.domain.exception.ContaNaoEncontradaException;
import com.banco.pagamento.application.domain.exception.SaldoInsuficienteException;
import com.banco.pagamento.ports.outbound.BancoCentralPort;
import com.banco.pagamento.ports.outbound.BoletoRepositoryPort;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.IdempotenciaPort;
import com.banco.pagamento.ports.outbound.NotificacaoKafkaPort;
import com.banco.pagamento.ports.outbound.TransacaoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcessarPagamentoUseCase")
class ProcessarPagamentoUseCaseTest {

    @Mock private ContaRepositoryPort contaRepository;
    @Mock private BoletoRepositoryPort boletoRepository;
    @Mock private BancoCentralPort bancoCentral;
    @Mock private NotificacaoKafkaPort notificacaoKafka;
    @Mock private IdempotenciaPort idempotencia;
    @Mock private TransacaoRepositoryPort transacaoRepository;

    private ProcessarPagamentoUseCase useCase;

    private static final String NUMERO_CONTA = "12345-6";
    private static final String CODIGO_BARRA = "23793380896012340900901613951001291070001500000";
    private static final BigDecimal SALDO_INICIAL = new BigDecimal("1000.00");
    private static final BigDecimal VALOR_BOLETO = new BigDecimal("250.00");

    @BeforeEach
    void setUp() {
        useCase = new ProcessarPagamentoUseCase(
            contaRepository, boletoRepository, bancoCentral, notificacaoKafka, idempotencia, transacaoRepository
        );
    }

    private Conta contaComSaldo(BigDecimal saldo) {
        return Conta.builder()
            .id(1L)
            .numeroConta(NUMERO_CONTA)
            .agencia("0001")
            .titular("João da Silva")
            .saldo(saldo)
            .versao(0L)
            .build();
    }

    private Boleto boletosPendente(BigDecimal valor) {
        return Boleto.builder()
            .id(1L)
            .codigoBarra(CODIGO_BARRA)
            .beneficiario("Empresa ABC")
            .pagador("João da Silva")
            .valor(valor)
            .dataVencimento(LocalDate.now().plusDays(30))
            .status(StatusBoleto.PENDENTE)
            .build();
    }

    @Nested
    @DisplayName("Pagamento bem-sucedido")
    class PagamentoBemSucedido {

        @BeforeEach
        void configurarMocks() {
            when(idempotencia.buscar(anyString())).thenReturn(Optional.empty());
            when(contaRepository.buscarComLockPessimista(NUMERO_CONTA))
                .thenReturn(Optional.of(contaComSaldo(SALDO_INICIAL)));
            when(boletoRepository.buscarPorCodigo(CODIGO_BARRA))
                .thenReturn(Optional.of(boletosPendente(VALOR_BOLETO)));
            doNothing().when(bancoCentral).validarBoleto(anyString());
            when(contaRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
            when(boletoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
            when(transacaoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
            doNothing().when(notificacaoKafka).publicar(any());
            doNothing().when(idempotencia).salvar(anyString(), any());
        }

        @Test
        @DisplayName("deve retornar resultado APROVADO com saldo debitado corretamente")
        void deveProcessarPagamentoComSucesso() {
            PagamentoComando comando = new PagamentoComando(NUMERO_CONTA, CODIGO_BARRA, UUID.randomUUID().toString());

            PagamentoResultado resultado = useCase.processar(comando);

            assertThat(resultado.status()).isEqualTo("APROVADO");
            assertThat(resultado.numeroConta()).isEqualTo(NUMERO_CONTA);
            assertThat(resultado.valorDebitado()).isEqualByComparingTo(VALOR_BOLETO);
            assertThat(resultado.reprocessado()).isFalse();
            assertThat(resultado.transacaoId()).isNotBlank();
        }

        @Test
        @DisplayName("deve publicar evento no outbox após o pagamento")
        void devePublicarEventoNoOutbox() {
            PagamentoComando comando = new PagamentoComando(NUMERO_CONTA, CODIGO_BARRA, UUID.randomUUID().toString());
            useCase.processar(comando);

            ArgumentCaptor<PagamentoEvento> captor = ArgumentCaptor.forClass(PagamentoEvento.class);
            verify(notificacaoKafka).publicar(captor.capture());

            PagamentoEvento evento = captor.getValue();
            assertThat(evento.tipo()).isEqualTo("BOLETO_PAGO");
            assertThat(evento.numeroConta()).isEqualTo(NUMERO_CONTA);
            assertThat(evento.valor()).isEqualByComparingTo(VALOR_BOLETO);
        }

        @Test
        @DisplayName("deve registrar transação de débito no extrato")
        void deveRegistrarTransacaoNoExtrato() {
            PagamentoComando comando = new PagamentoComando(NUMERO_CONTA, CODIGO_BARRA, UUID.randomUUID().toString());
            useCase.processar(comando);

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
            verify(transacaoRepository).salvar(captor.capture());

            Transacao transacao = captor.getValue();
            assertThat(transacao.getTipo()).isEqualTo(TipoTransacao.DEBITO);
            assertThat(transacao.getNumeroConta()).isEqualTo(NUMERO_CONTA);
            assertThat(transacao.getValor()).isEqualByComparingTo(VALOR_BOLETO);
            assertThat(transacao.getSaldoApos()).isEqualByComparingTo(new BigDecimal("750.00"));
            assertThat(transacao.getCategoria()).isEqualTo("Boleto");
        }

        @Test
        @DisplayName("deve salvar a chave de idempotência após pagamento")
        void deveSalvarChaveIdempotencia() {
            String chave = UUID.randomUUID().toString();
            PagamentoComando comando = new PagamentoComando(NUMERO_CONTA, CODIGO_BARRA, chave);
            useCase.processar(comando);

            verify(idempotencia).salvar(eq(chave), any());
        }
    }

    @Nested
    @DisplayName("Idempotência")
    class Idempotencia {

        @Test
        @DisplayName("deve retornar resultado cacheado sem reprocessar quando chave já existir")
        void deveRetornarResultadoCacheado() {
            String chave = UUID.randomUUID().toString();
            PagamentoResultado resultadoCacheado = new PagamentoResultado(
                UUID.randomUUID().toString(), NUMERO_CONTA, CODIGO_BARRA,
                VALOR_BOLETO, null, "APROVADO", false
            );
            when(idempotencia.buscar(chave)).thenReturn(Optional.of(resultadoCacheado));

            PagamentoComando comando = new PagamentoComando(NUMERO_CONTA, CODIGO_BARRA, chave);
            PagamentoResultado resultado = useCase.processar(comando);

            assertThat(resultado.reprocessado()).isTrue();
            assertThat(resultado.transacaoId()).isEqualTo(resultadoCacheado.transacaoId());
            verify(contaRepository, never()).buscarComLockPessimista(anyString());
            verify(notificacaoKafka, never()).publicar(any());
        }
    }

    @Nested
    @DisplayName("Validações de negócio")
    class ValidacoesNegocio {

        @BeforeEach
        void configurarIdempotencia() {
            when(idempotencia.buscar(anyString())).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("deve lançar ContaNaoEncontradaException quando conta não existir")
        void deveLancarExcecaoQuandoContaNaoEncontrada() {
            when(contaRepository.buscarComLockPessimista(NUMERO_CONTA)).thenReturn(Optional.empty());

            PagamentoComando comando = new PagamentoComando(NUMERO_CONTA, CODIGO_BARRA, UUID.randomUUID().toString());

            assertThatThrownBy(() -> useCase.processar(comando))
                .isInstanceOf(ContaNaoEncontradaException.class);
        }

        @Test
        @DisplayName("deve lançar BoletoNaoEncontradoException quando boleto não existir")
        void deveLancarExcecaoQuandoBoletoNaoEncontrado() {
            when(contaRepository.buscarComLockPessimista(NUMERO_CONTA))
                .thenReturn(Optional.of(contaComSaldo(SALDO_INICIAL)));
            when(boletoRepository.buscarPorCodigo(CODIGO_BARRA)).thenReturn(Optional.empty());

            PagamentoComando comando = new PagamentoComando(NUMERO_CONTA, CODIGO_BARRA, UUID.randomUUID().toString());

            assertThatThrownBy(() -> useCase.processar(comando))
                .isInstanceOf(BoletoNaoEncontradoException.class);
        }

        @Test
        @DisplayName("deve lançar SaldoInsuficienteException quando saldo for menor que o valor do boleto")
        void deveLancarExcecaoQuandoSaldoInsuficiente() {
            BigDecimal saldoBaixo = new BigDecimal("100.00");
            when(contaRepository.buscarComLockPessimista(NUMERO_CONTA))
                .thenReturn(Optional.of(contaComSaldo(saldoBaixo)));
            when(boletoRepository.buscarPorCodigo(CODIGO_BARRA))
                .thenReturn(Optional.of(boletosPendente(VALOR_BOLETO)));
            doNothing().when(bancoCentral).validarBoleto(anyString());

            PagamentoComando comando = new PagamentoComando(NUMERO_CONTA, CODIGO_BARRA, UUID.randomUUID().toString());

            assertThatThrownBy(() -> useCase.processar(comando))
                .isInstanceOf(SaldoInsuficienteException.class);
        }

        @Test
        @DisplayName("deve lançar BoletoJaPagoException quando boleto não estiver PENDENTE")
        void deveLancarExcecaoQuandoBoletoJaPago() {
            Boleto boletoJaPago = Boleto.builder()
                .id(1L).codigoBarra(CODIGO_BARRA).beneficiario("Empresa ABC")
                .pagador("João").valor(VALOR_BOLETO)
                .dataVencimento(LocalDate.now().plusDays(30))
                .status(StatusBoleto.PAGO)
                .build();
            when(contaRepository.buscarComLockPessimista(NUMERO_CONTA))
                .thenReturn(Optional.of(contaComSaldo(SALDO_INICIAL)));
            when(boletoRepository.buscarPorCodigo(CODIGO_BARRA))
                .thenReturn(Optional.of(boletoJaPago));
            doNothing().when(bancoCentral).validarBoleto(anyString());

            PagamentoComando comando = new PagamentoComando(NUMERO_CONTA, CODIGO_BARRA, UUID.randomUUID().toString());

            assertThatThrownBy(() -> useCase.processar(comando))
                .isInstanceOf(BoletoJaPagoException.class);
        }
    }
}
