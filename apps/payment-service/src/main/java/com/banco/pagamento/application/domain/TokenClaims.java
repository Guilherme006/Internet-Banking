package com.banco.pagamento.application.domain;

public record TokenClaims(
    Long usuarioId,
    String jti,
    String tipo
) {
}
