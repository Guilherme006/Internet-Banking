package com.banco.pagamento.ports.inbound;

import com.banco.pagamento.application.usecase.SessaoComando;

public interface EncerrarSessaoPort {

    void encerrar(SessaoComando comando);
}
