package com.banco.pagamento.adapters.outbound.external;

import com.banco.pagamento.adapters.outbound.external.exception.ServicoExternoException;
import com.banco.pagamento.ports.outbound.BancoCentralPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class BancoCentralAdapter implements BancoCentralPort {

    private static final Random RANDOM = new Random();

    @Override
    @CircuitBreaker(name = "bancoCentral", fallbackMethod = "fallbackValidarBoleto")
    @Retry(name = "bancoCentral")
    public void validarBoleto(String codigoBarra) {
        log.debug("Consultando Banco Central para validar boleto: {}", codigoBarra);

        if (RANDOM.nextInt(10) < 3) {
            throw new ServicoExternoException(
                "Banco Central API indisponível (simulação). Boleto: " + codigoBarra);
        }

        log.debug("Boleto validado com sucesso pelo Banco Central: {}", codigoBarra);
    }

        @SuppressWarnings("unused")
    private void fallbackValidarBoleto(String codigoBarra, Throwable throwable) {
        log.warn("Circuit Breaker ABERTO para BancoCentral | boleto={} | causa={}",
            codigoBarra, throwable.getMessage());
        log.warn("Aplicando política fail-open: assumindo boleto válido e prosseguindo com o pagamento.");
    }
}
