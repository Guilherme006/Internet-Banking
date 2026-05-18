package com.banco.pagamento.ports.inbound;

import com.banco.pagamento.application.usecase.AutenticacaoResultado;
import com.banco.pagamento.application.usecase.SessaoComando;

public interface RenovarSessaoPort {

    AutenticacaoResultado renovar(SessaoComando comando);
}
