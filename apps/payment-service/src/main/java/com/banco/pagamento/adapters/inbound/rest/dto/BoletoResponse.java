package com.banco.pagamento.adapters.inbound.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BoletoResponse(
    String codigoBarra,
    String beneficiario,
    String pagador,
    BigDecimal valor,
    LocalDate dataVencimento,
    String status
) {}
