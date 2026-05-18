package com.banco.pagamento.adapters.config;

import com.banco.pagamento.application.usecase.ConsultarBoletoUseCase;
import com.banco.pagamento.application.usecase.ConsultarExtratoUseCase;
import com.banco.pagamento.application.usecase.PagamentoComando;
import com.banco.pagamento.application.usecase.PagamentoResultado;
import com.banco.pagamento.application.usecase.ProcessarPagamentoUseCase;
import com.banco.pagamento.ports.inbound.ConsultarBoletoPort;
import com.banco.pagamento.ports.inbound.ConsultarExtratoPort;
import com.banco.pagamento.ports.inbound.ProcessarPagamentoPort;
import com.banco.pagamento.ports.outbound.BancoCentralPort;
import com.banco.pagamento.ports.outbound.BoletoRepositoryPort;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.IdempotenciaPort;
import com.banco.pagamento.ports.outbound.NotificacaoKafkaPort;
import com.banco.pagamento.ports.outbound.TransacaoRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class UseCaseConfig {

    @Bean
    public ProcessarPagamentoUseCase processarPagamentoUseCase(
            ContaRepositoryPort contaRepositoryPort,
            BoletoRepositoryPort boletoRepositoryPort,
            BancoCentralPort bancoCentralPort,
            NotificacaoKafkaPort notificacaoKafkaPort,
            IdempotenciaPort idempotenciaPort,
            TransacaoRepositoryPort transacaoRepositoryPort) {
        return new ProcessarPagamentoUseCase(
            contaRepositoryPort,
            boletoRepositoryPort,
            bancoCentralPort,
            notificacaoKafkaPort,
            idempotenciaPort,
            transacaoRepositoryPort
        );
    }

    @Bean
    public ConsultarBoletoPort consultarBoletoPort(BoletoRepositoryPort boletoRepositoryPort) {
        return new ConsultarBoletoUseCase(boletoRepositoryPort);
    }

    @Bean
    public ConsultarExtratoPort consultarExtratoPort(TransacaoRepositoryPort transacaoRepositoryPort) {
        return new ConsultarExtratoUseCase(transacaoRepositoryPort);
    }

    @Bean
    public ProcessarPagamentoPort processarPagamentoPort(
            ProcessarPagamentoUseCase processarPagamentoUseCase,
            TransactionTemplate transactionTemplate) {
        return (PagamentoComando comando) -> {
            PagamentoResultado resultado = transactionTemplate.execute(
                status -> processarPagamentoUseCase.processar(comando)
            );
            if (resultado == null) {
                throw new IllegalStateException("Use case retornou resultado nulo inesperadamente");
            }
            return resultado;
        };
    }
}
