package com.banco.pagamento.application.usecase;

public record EnderecoComando(
    String cep,
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String uf
) {
}
