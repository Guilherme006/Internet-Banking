package com.banco.pagamento.application.usecase;

import com.banco.pagamento.ports.inbound.ConsultarExtratoPort;
import com.banco.pagamento.ports.outbound.TransacaoRepositoryPort;

public class ConsultarExtratoUseCase implements ConsultarExtratoPort {

    private final TransacaoRepositoryPort transacaoRepository;

    public ConsultarExtratoUseCase(TransacaoRepositoryPort transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    @Override
    public ExtratoResultado consultar(ConsultarExtratoQuery query) {
        return transacaoRepository.buscarExtrato(query);
    }
}
