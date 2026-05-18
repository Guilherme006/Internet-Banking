package com.banco.pagamento.adapters.inbound.rest.dto;

import java.util.List;

public record ExtratoResponse(
    List<TransacaoResponse> content,
    long totalElements,
    int number,
    int size,
    int totalPages
) {}
