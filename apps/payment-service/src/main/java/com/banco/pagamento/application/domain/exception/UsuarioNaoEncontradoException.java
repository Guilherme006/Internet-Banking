package com.banco.pagamento.application.domain.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException() {
        super("Usuário autenticado não encontrado.");
    }
}
