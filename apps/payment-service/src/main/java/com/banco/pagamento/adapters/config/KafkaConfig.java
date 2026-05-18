package com.banco.pagamento.adapters.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.pagamento-boleto}")
    private String topicoPagamentoBoleto;

    @Bean
    public NewTopic topicoPagamentoBoletoBean() {
        return TopicBuilder.name(topicoPagamentoBoleto)
            .partitions(3)
            .replicas(1)
            .build();
    }
}
