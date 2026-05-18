package com.banco.pagamento.adapters.security;

import com.banco.pagamento.application.domain.AuditoriaEvento;
import com.banco.pagamento.ports.outbound.AuditoriaPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaPort auditoriaPort;

    public void registrar(String acao, String status, Long usuarioId, String email, HttpServletRequest request, String detalhes) {
        auditoriaPort.registrar(AuditoriaEvento.builder()
            .usuarioId(usuarioId)
            .email(normalizarEmail(email))
            .acao(acao)
            .status(status)
            .ip(clientIp(request))
            .userAgent(truncar(request == null ? null : request.getHeader("User-Agent"), 255))
            .detalhes(truncar(detalhes, 500))
            .criadoEm(LocalDateTime.now())
            .build());
    }

    private String clientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String truncar(String valor, int limite) {
        if (valor == null || valor.length() <= limite) {
            return valor;
        }
        return valor.substring(0, limite);
    }
}
