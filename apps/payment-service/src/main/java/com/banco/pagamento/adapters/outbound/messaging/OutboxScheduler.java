package com.banco.pagamento.adapters.outbound.messaging;

import com.banco.pagamento.adapters.outbound.persistence.entity.OutboxEntity;
import com.banco.pagamento.adapters.outbound.persistence.repository.OutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxJpaRepository outboxJpaRepository;
    private final PagamentoKafkaProducer kafkaProducer;

    @Value("${kafka.topics.pagamento-boleto}")
    private String topicoPagamentoBoleto;

    @Scheduled(fixedDelayString = "${outbox.scheduler.fixed-delay-ms:5000}")
    @Transactional
    public void processarEventosPendentes() {
        List<OutboxEntity> pendentes = outboxJpaRepository
            .findTop50ByProcessadoFalseOrderByCriadoEmAsc();

        if (pendentes.isEmpty()) {
            return;
        }

        log.info("Outbox Scheduler: {} evento(s) pendente(s) encontrado(s)", pendentes.size());

        for (OutboxEntity evento : pendentes) {
            try {
                kafkaProducer.enviar(resolverTopico(evento.getTipo()), evento.getEventoId(), evento.getPayload());
                evento.setProcessado(true);
                evento.setProcessadoEm(LocalDateTime.now());
                outboxJpaRepository.save(evento);
                log.info("Evento publicado no Kafka | eventoId={} | tipo={}", evento.getEventoId(), evento.getTipo());
            } catch (Exception e) {
                log.error("Falha ao publicar evento do outbox | eventoId={} | tentativa será feita novamente",
                    evento.getEventoId(), e);
            }
        }
    }

    private String resolverTopico(String tipo) {
        return switch (tipo) {
            case "BOLETO_PAGO" -> topicoPagamentoBoleto;
            default -> topicoPagamentoBoleto;
        };
    }
}
