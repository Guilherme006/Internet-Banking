package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Transacao;

import java.util.List;

public record ExtratoResultado(
    List<Transacao> content,
    long totalElements,
    int number,
    int size,
    int totalPages
) {}
