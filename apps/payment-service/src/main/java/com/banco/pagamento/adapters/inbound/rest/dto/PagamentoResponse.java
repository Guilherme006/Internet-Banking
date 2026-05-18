package com.banco.pagamento.adapters.inbound.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoResponse(
    String transacaoId,
    String numeroConta,
    String codigoBarra,
    BigDecimal valorDebitado,
    LocalDateTime dataHora,
    String status,
    boolean reprocessado
) {}
