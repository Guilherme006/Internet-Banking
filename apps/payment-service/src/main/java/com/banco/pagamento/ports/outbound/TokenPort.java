package com.banco.pagamento.ports.outbound;

import com.banco.pagamento.application.domain.TokenClaims;
import com.banco.pagamento.application.domain.Usuario;

import java.util.Optional;

public interface TokenPort {

    String gerarAccessToken(Usuario usuario);

    String gerarRefreshToken(Usuario usuario, String jti);

    Optional<TokenClaims> extrairAccessToken(String token);

    Optional<TokenClaims> extrairRefreshToken(String token);
}
