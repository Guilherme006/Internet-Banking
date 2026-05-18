package com.banco.pagamento.application.domain.exception;

public class BoletoJaPagoException extends RuntimeException {

    public BoletoJaPagoException(String message) {
        super(message);
    }
}
