package com.banco.pagamento.adapters.outbound.security;

import com.banco.pagamento.application.domain.TokenClaims;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.ports.outbound.TokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenAdapter implements TokenPort {

    private static final String TYPE_CLAIM = "typ";
    private static final String ACCESS_TYPE = "access";
    private static final String REFRESH_TYPE = "refresh";

    private final SecretKey secretKey;
    private final Duration accessTtl;
    private final Duration refreshTtl;

    public JwtTokenAdapter(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes}") long expirationMinutes,
            @Value("${security.jwt.refresh-expiration-days}") long refreshExpirationDays) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtl = Duration.ofMinutes(expirationMinutes);
        this.refreshTtl = Duration.ofDays(refreshExpirationDays);
    }

    @Override
    public String gerarAccessToken(Usuario usuario) {
        return gerar(usuario, null, ACCESS_TYPE, accessTtl);
    }

    @Override
    public String gerarRefreshToken(Usuario usuario, String jti) {
        return gerar(usuario, jti, REFRESH_TYPE, refreshTtl);
    }

    @Override
    public Optional<TokenClaims> extrairAccessToken(String token) {
        return extrair(token, ACCESS_TYPE);
    }

    @Override
    public Optional<TokenClaims> extrairRefreshToken(String token) {
        return extrair(token, REFRESH_TYPE);
    }

    private String gerar(Usuario usuario, String jti, String tipo, Duration ttl) {
        Instant agora = Instant.now();
        return Jwts.builder()
            .subject(String.valueOf(usuario.getId()))
            .id(jti)
            .claim(TYPE_CLAIM, tipo)
            .claim("email", usuario.getEmail())
            .claim("nome", usuario.getNome())
            .issuedAt(Date.from(agora))
            .expiration(Date.from(agora.plus(ttl)))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    private Optional<TokenClaims> extrair(String token, String tipoEsperado) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            if (!tipoEsperado.equals(claims.get(TYPE_CLAIM, String.class))) {
                return Optional.empty();
            }

            return Optional.of(new TokenClaims(
                Long.valueOf(claims.getSubject()),
                claims.getId(),
                tipoEsperado
            ));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
