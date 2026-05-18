package com.banco.pagamento.application.usecase;

public record UsuarioResultado(
    Long id,
    String nome,
    String email,
    String cpf,
    String agencia,
    String conta
) {
}
