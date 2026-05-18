package com.banco.pagamento.application.usecase;

public record AutenticacaoResultado(
    String accessToken,
    String refreshToken,
    String tipo,
    long expiraEmSegundos,
    long refreshExpiraEmSegundos,
    UsuarioResultado usuario
) {
}
