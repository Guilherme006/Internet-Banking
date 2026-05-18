package com.banco.pagamento.application.domain.exception;

public class CadastroInvalidoException extends RuntimeException {

    public CadastroInvalidoException(String mensagem) {
        super(mensagem);
    }
}
