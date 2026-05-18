package com.banco.pagamento.application.usecase;

public record LoginComando(
    String email,
    String senha
) {
}
