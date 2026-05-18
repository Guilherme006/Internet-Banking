package com.banco.pagamento.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoResultado(
    String transacaoId,
    String numeroConta,
    String codigoBarra,
    BigDecimal valorDebitado,
    LocalDateTime dataHora,
    String status,
    boolean reprocessado
) {}
