package com.banco.pagamento.ports.outbound;

public interface BancoCentralPort {

        void validarBoleto(String codigoBarra);
}
