package com.banco.pagamento.application.domain.exception;

public class UsuarioJaCadastradoException extends RuntimeException {

    public UsuarioJaCadastradoException(String campo) {
        super("Já existe usuário cadastrado com este " + campo + ".");
    }
}
