package com.banco.pagamento.ports.inbound;

import com.banco.pagamento.application.usecase.PagamentoComando;
import com.banco.pagamento.application.usecase.PagamentoResultado;

public interface ProcessarPagamentoPort {

    PagamentoResultado processar(PagamentoComando comando);
}
