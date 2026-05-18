package com.banco.pagamento.application.usecase;

public record CadastroUsuarioComando(
    String nome,
    String email,
    String cpf,
    String senha,
    EnderecoComando endereco
) {
}
