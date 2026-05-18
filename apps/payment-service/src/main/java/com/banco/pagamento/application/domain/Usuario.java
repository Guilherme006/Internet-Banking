package com.banco.pagamento.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Usuario {

    private final Long id;
    private final String nome;
    private final String email;
    private final String cpf;
    private final String senhaHash;
    private final String numeroConta;
    private final Endereco endereco;
}
