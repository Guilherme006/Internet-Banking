package com.banco.pagamento.ports.inbound;

import com.banco.pagamento.application.usecase.AutenticacaoResultado;
import com.banco.pagamento.application.usecase.CadastroUsuarioComando;

public interface CadastrarUsuarioPort {

    AutenticacaoResultado cadastrar(CadastroUsuarioComando comando);
}
