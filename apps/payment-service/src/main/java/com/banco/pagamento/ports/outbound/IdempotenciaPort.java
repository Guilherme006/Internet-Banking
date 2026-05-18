package com.banco.pagamento.ports.outbound;

import com.banco.pagamento.application.usecase.PagamentoResultado;

import java.util.Optional;

public interface IdempotenciaPort {

    Optional<PagamentoResultado> buscar(String chave);

    void salvar(String chave, PagamentoResultado resultado);
}
