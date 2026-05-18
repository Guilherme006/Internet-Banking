package com.banco.pagamento.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AuditoriaEvento {

    private final Long id;
    private final Long usuarioId;
    private final String email;
    private final String acao;
    private final String status;
    private final String ip;
    private final String userAgent;
    private final String detalhes;
    private final LocalDateTime criadoEm;
}
