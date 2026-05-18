package com.banco.pagamento.application.domain.exception;

public class SessaoInvalidaException extends RuntimeException {

    public SessaoInvalidaException() {
        super("Sessão inválida ou expirada.");
    }
}
