package com.banco.pagamento.adapters.outbound.security;

import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.ports.outbound.TokenPort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtTokenAdapter implements TokenPort {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final long expirationMinutes;

    public JwtTokenAdapter(
            ObjectMapper objectMapper,
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes}") long expirationMinutes) {
        this.objectMapper = objectMapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public String gerar(Usuario usuario) {
        Instant agora = Instant.now();
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", usuario.getId());
        payload.put("email", usuario.getEmail());
        payload.put("nome", usuario.getNome());
        payload.put("iat", agora.getEpochSecond());
        payload.put("exp", agora.plusSeconds(expirationMinutes * 60).getEpochSecond());

        String unsignedToken = encodeJson(header) + "." + encodeJson(payload);
        return unsignedToken + "." + assinar(unsignedToken);
    }

    public Optional<Long> extrairUsuarioId(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                return Optional.empty();
            }

            String unsignedToken = partes[0] + "." + partes[1];
            if (!assinar(unsignedToken).equals(partes[2])) {
                return Optional.empty();
            }

            Map<String, Object> payload = objectMapper.readValue(
                DECODER.decode(partes[1]),
                new TypeReference<>() {
                }
            );

            Number exp = (Number) payload.get("exp");
            if (exp == null || exp.longValue() < Instant.now().getEpochSecond()) {
                return Optional.empty();
            }

            Number sub = (Number) payload.get("sub");
            return sub == null ? Optional.empty() : Optional.of(sub.longValue());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao gerar token.", ex);
        }
    }

    private String assinar(String conteudo) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return ENCODER.encodeToString(mac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao assinar token.", ex);
        }
    }
}
