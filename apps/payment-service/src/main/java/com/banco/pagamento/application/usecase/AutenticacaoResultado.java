package com.banco.pagamento.application.usecase;

public record AutenticacaoResultado(
    String token,
    String tipo,
    long expiraEmSegundos,
    UsuarioResultado usuario
) {
}
