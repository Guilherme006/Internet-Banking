package com.banco.pagamento.ports.inbound;

import com.banco.pagamento.application.domain.Boleto;

public interface ConsultarBoletoPort {

    Boleto consultar(String codigoBarra);
}
