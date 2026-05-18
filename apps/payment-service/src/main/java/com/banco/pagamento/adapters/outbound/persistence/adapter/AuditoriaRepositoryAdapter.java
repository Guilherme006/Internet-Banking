package com.banco.pagamento.adapters.outbound.persistence.adapter;

import com.banco.pagamento.adapters.outbound.persistence.entity.AuditoriaEntity;
import com.banco.pagamento.adapters.outbound.persistence.repository.AuditoriaJpaRepository;
import com.banco.pagamento.application.domain.AuditoriaEvento;
import com.banco.pagamento.ports.outbound.AuditoriaPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditoriaRepositoryAdapter implements AuditoriaPort {

    private final AuditoriaJpaRepository auditoriaJpaRepository;

    @Override
    public void registrar(AuditoriaEvento evento) {
        try {
            auditoriaJpaRepository.save(AuditoriaEntity.builder()
                .usuarioId(evento.getUsuarioId())
                .email(evento.getEmail())
                .acao(evento.getAcao())
                .status(evento.getStatus())
                .ip(evento.getIp())
                .userAgent(evento.getUserAgent())
                .detalhes(evento.getDetalhes())
                .criadoEm(evento.getCriadoEm() == null ? LocalDateTime.now() : evento.getCriadoEm())
                .build());
        } catch (RuntimeException ex) {
            log.warn("Falha ao registrar auditoria | acao={} | status={}", evento.getAcao(), evento.getStatus(), ex);
        }
    }
}
