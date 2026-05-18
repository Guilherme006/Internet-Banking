package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Boleto;
import com.banco.pagamento.application.domain.exception.BoletoNaoEncontradoException;
import com.banco.pagamento.ports.inbound.ConsultarBoletoPort;
import com.banco.pagamento.ports.outbound.BoletoRepositoryPort;

public class ConsultarBoletoUseCase implements ConsultarBoletoPort {

    private final BoletoRepositoryPort boletoRepository;

    public ConsultarBoletoUseCase(BoletoRepositoryPort boletoRepository) {
        this.boletoRepository = boletoRepository;
    }

    @Override
    public Boleto consultar(String codigoBarra) {
        return boletoRepository.buscarPorCodigo(codigoBarra)
            .orElseThrow(() -> new BoletoNaoEncontradoException(codigoBarra));
    }
}
