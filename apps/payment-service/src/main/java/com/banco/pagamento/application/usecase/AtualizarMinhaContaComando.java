package com.banco.pagamento.application.usecase;

public record AtualizarMinhaContaComando(
    Long usuarioId,
    String nome,
    String email,
    EnderecoComando endereco
) {
}
