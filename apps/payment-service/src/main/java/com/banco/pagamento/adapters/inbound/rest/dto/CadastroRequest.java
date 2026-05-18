package com.banco.pagamento.adapters.inbound.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CadastroRequest(
    @NotBlank @Size(min = 3, max = 120)
    String nome,

    @NotBlank @Email @Size(max = 160)
    String email,

    @NotBlank @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    String cpf,

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,72}$",
        message = "Senha deve ter ao menos 8 caracteres, com maiúscula, minúscula, número e símbolo"
    )
    String senha,

    @NotNull @Valid
    EnderecoRequest endereco
) {
}
