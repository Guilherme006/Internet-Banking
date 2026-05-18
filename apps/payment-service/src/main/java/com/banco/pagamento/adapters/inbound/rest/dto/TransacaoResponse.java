package com.banco.pagamento.adapters.inbound.rest.dto;

import com.banco.pagamento.application.domain.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponse(
    String id,
    TipoTransacao tipo,
    String descricao,
    BigDecimal valor,
    LocalDateTime dataHora,
    BigDecimal saldoApos,
    String categoria
) {}
