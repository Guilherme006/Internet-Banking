package com.banco.pagamento.ports.inbound;

import com.banco.pagamento.application.usecase.AutenticacaoResultado;
import com.banco.pagamento.application.usecase.LoginComando;

public interface AutenticarUsuarioPort {

    AutenticacaoResultado autenticar(LoginComando comando);
}
