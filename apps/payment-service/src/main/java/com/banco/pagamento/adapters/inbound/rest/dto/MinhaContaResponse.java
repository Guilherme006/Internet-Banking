package com.banco.pagamento.adapters.inbound.rest.dto;

import java.math.BigDecimal;

public record MinhaContaResponse(
    Long id,
    String nome,
    String email,
    String cpf,
    String agencia,
    String conta,
    BigDecimal saldo,
    EnderecoRequest endereco
) {
}
