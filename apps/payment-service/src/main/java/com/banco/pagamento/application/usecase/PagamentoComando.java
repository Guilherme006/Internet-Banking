package com.banco.pagamento.application.usecase;

public record PagamentoComando(
    String numeroConta,
    String codigoBarra,
    String chaveIdempotencia
) {}
