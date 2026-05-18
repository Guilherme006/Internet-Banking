package com.banco.pagamento.ports.inbound;

import com.banco.pagamento.application.usecase.ConsultarExtratoQuery;
import com.banco.pagamento.application.usecase.ExtratoResultado;

public interface ConsultarExtratoPort {

    ExtratoResultado consultar(ConsultarExtratoQuery query);
}
