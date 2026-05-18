package com.banco.pagamento.application.domain.exception;

public class MuitasTentativasLoginException extends RuntimeException {

    public MuitasTentativasLoginException() {
        super("Muitas tentativas de login. Aguarde alguns minutos e tente novamente.");
    }
}
