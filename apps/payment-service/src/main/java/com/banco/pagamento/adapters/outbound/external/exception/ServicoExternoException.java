package com.banco.pagamento.adapters.outbound.external.exception;

public class ServicoExternoException extends RuntimeException {

    public ServicoExternoException(String message) {
        super(message);
    }

    public ServicoExternoException(String message, Throwable cause) {
        super(message, cause);
    }
}
