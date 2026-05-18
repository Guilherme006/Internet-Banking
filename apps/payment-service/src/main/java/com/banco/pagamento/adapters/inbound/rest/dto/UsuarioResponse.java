package com.banco.pagamento.adapters.inbound.rest.dto;

public record UsuarioResponse(
    Long id,
    String nome,
    String email,
    String cpf,
    String agencia,
    String conta
) {
}
