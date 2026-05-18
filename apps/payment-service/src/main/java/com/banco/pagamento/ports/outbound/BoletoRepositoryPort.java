package com.banco.pagamento.ports.outbound;

import com.banco.pagamento.application.domain.Boleto;

import java.util.Optional;

public interface BoletoRepositoryPort {

    Optional<Boleto> buscarPorCodigo(String codigoBarra);

    Boleto salvar(Boleto boleto);
}
