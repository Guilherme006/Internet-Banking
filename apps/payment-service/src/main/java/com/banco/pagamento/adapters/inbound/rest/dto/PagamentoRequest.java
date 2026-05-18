package com.banco.pagamento.adapters.inbound.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PagamentoRequest(

    @NotBlank(message = "O número da conta é obrigatório")
    @Pattern(regexp = "\\d{5}-\\d", message = "Formato inválido. Use: 12345-6")
    String numeroConta,

    @NotBlank(message = "O código de barras do boleto é obrigatório")
    @Pattern(regexp = "\\d{44,48}", message = "Código de barras deve conter entre 44 e 48 dígitos")
    String codigoBarra
) {}
