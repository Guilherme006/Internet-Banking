package com.banco.pagamento.application.usecase;

import java.math.BigDecimal;

public record MinhaContaResultado(
    Long id,
    String nome,
    String email,
    String cpf,
    String agencia,
    String conta,
    BigDecimal saldo,
    EnderecoComando endereco
) {
}
