package com.banco.pagamento.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoEvento(
    String transacaoId,
    String tipo,
    String numeroConta,
    String codigoBarra,
    BigDecimal valor,
    LocalDateTime dataHora
) {}
