package com.banco.pagamento.adapters.inbound.rest.dto;

public record AuthResponse(
    String token,
    String tipo,
    long expiraEmSegundos,
    UsuarioResponse usuario
) {
}
