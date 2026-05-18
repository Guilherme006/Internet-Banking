package com.banco.pagamento.adapters.inbound.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarMinhaContaRequest(
    @NotBlank
    @Size(min = 3, max = 120)
    String nome,

    @NotBlank
    @Email
    @Size(max = 160)
    String email,

    @Valid
    @NotNull
    EnderecoRequest endereco
) {
}
