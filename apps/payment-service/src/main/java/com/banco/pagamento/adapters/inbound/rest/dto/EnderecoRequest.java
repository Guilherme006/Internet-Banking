package com.banco.pagamento.adapters.inbound.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(
    @NotBlank @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    String cep,

    @NotBlank @Size(max = 140)
    String logradouro,

    @NotBlank @Size(max = 20)
    String numero,

    @Size(max = 80)
    String complemento,

    @NotBlank @Size(max = 80)
    String bairro,

    @NotBlank @Size(max = 80)
    String cidade,

    @NotBlank @Pattern(regexp = "[A-Z]{2}", message = "UF deve conter 2 letras maiúsculas")
    String uf
) {
}
