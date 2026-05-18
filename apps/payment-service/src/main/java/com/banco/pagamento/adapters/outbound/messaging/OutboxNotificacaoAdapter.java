package com.banco.pagamento.adapters.outbound.messaging;

import com.banco.pagamento.adapters.outbound.persistence.entity.OutboxEntity;
import com.banco.pagamento.adapters.outbound.persistence.repository.OutboxJpaRepository;
import com.banco.pagamento.application.usecase.PagamentoEvento;
import com.banco.pagamento.ports.outbound.NotificacaoKafkaPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxNotificacaoAdapter implements NotificacaoKafkaPort {

    private final OutboxJpaRepository outboxJpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publicar(PagamentoEvento evento) {
        try {
            String payload = objectMapper.writeValueAsString(evento);
            OutboxEntity outbox = OutboxEntity.builder()
                .eventoId(evento.transacaoId())
                .tipo(evento.tipo())
                .payload(payload)
                .processado(false)
                .criadoEm(LocalDateTime.now())
                .build();
            outboxJpaRepository.save(outbox);
            log.debug("Evento salvo no outbox | tipo={} | transacaoId={}", evento.tipo(), evento.transacaoId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Falha ao serializar evento para o outbox: " + evento.transacaoId(), e);
        }
    }
}
