package com.banco.pagamento.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class RefreshToken {

    private Long id;
    private String jti;
    private Long usuarioId;
    private LocalDateTime expiraEm;
    private boolean revogado;
}
