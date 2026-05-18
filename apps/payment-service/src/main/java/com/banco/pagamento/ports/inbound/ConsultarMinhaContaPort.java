package com.banco.pagamento.ports.inbound;

import com.banco.pagamento.application.usecase.MinhaContaResultado;

public interface ConsultarMinhaContaPort {

    MinhaContaResultado consultar(Long usuarioId);
}
