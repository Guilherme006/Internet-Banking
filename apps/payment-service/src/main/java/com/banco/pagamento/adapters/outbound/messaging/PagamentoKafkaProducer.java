package com.banco.pagamento.adapters.outbound.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagamentoKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void enviar(String topico, String chaveParticionamento, String payload) {
        CompletableFuture<SendResult<String, String>> future =
            kafkaTemplate.send(topico, chaveParticionamento, payload);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Falha ao enviar mensagem ao Kafka | topico={} | chave={} | erro={}",
                    topico, chaveParticionamento, ex.getMessage());
            } else {
                log.debug("Mensagem enviada ao Kafka | topico={} | partition={} | offset={}",
                    topico,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            }
        });
    }
}
