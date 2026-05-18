package com.banco.pagamento.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class Transacao {

    private final String id;
    private final String numeroConta;
    private final TipoTransacao tipo;
    private final String descricao;
    private final BigDecimal valor;
    private final LocalDateTime dataHora;
    private final BigDecimal saldoApos;
    private final String categoria;
}
