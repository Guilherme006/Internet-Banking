package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.TipoTransacao;

import java.time.LocalDateTime;

public record ConsultarExtratoQuery(
    String numeroConta,
    TipoTransacao tipo,
    LocalDateTime dataInicio,
    LocalDateTime dataFim,
    int pagina,
    int tamanho
) {}
