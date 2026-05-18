package com.banco.pagamento.ports.inbound;

import com.banco.pagamento.application.usecase.AtualizarMinhaContaComando;
import com.banco.pagamento.application.usecase.MinhaContaResultado;

public interface AtualizarMinhaContaPort {

    MinhaContaResultado atualizar(AtualizarMinhaContaComando comando);
}
