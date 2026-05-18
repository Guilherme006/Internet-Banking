package com.banco.pagamento.adapters.inbound.rest;

import com.banco.pagamento.adapters.inbound.rest.dto.ExtratoResponse;
import com.banco.pagamento.adapters.inbound.rest.dto.TransacaoResponse;
import com.banco.pagamento.application.domain.TipoTransacao;
import com.banco.pagamento.application.domain.Transacao;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.usecase.ConsultarExtratoQuery;
import com.banco.pagamento.application.usecase.ExtratoResultado;
import com.banco.pagamento.ports.inbound.ConsultarExtratoPort;
import com.banco.pagamento.ports.inbound.ConsultarUsuarioAutenticadoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/extrato")
@RequiredArgsConstructor
public class ExtratoController {

    private final ConsultarExtratoPort consultarExtratoPort;
    private final ConsultarUsuarioAutenticadoPort consultarUsuarioAutenticadoPort;

    @GetMapping
    public ExtratoResponse consultar(
            @RequestParam(value = "tipo", required = false) TipoTransacao tipo,
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime dataInicio,
            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime dataFim,
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") int tamanho,
            Authentication authentication) {

        Usuario usuario = consultarUsuarioAutenticadoPort.consultar(usuarioId(authentication));

        ConsultarExtratoQuery query = new ConsultarExtratoQuery(
            usuario.getNumeroConta(),
            tipo,
            toLocalDateTime(dataInicio),
            toLocalDateTime(dataFim),
            pagina,
            tamanho
        );

        return toResponse(consultarExtratoPort.consultar(query));
    }

    private LocalDateTime toLocalDateTime(OffsetDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDateTime();
    }

    private ExtratoResponse toResponse(ExtratoResultado resultado) {
        return new ExtratoResponse(
            resultado.content().stream().map(this::toResponse).toList(),
            resultado.totalElements(),
            resultado.number(),
            resultado.size(),
            resultado.totalPages()
        );
    }

    private TransacaoResponse toResponse(Transacao transacao) {
        return new TransacaoResponse(
            transacao.getId(),
            transacao.getTipo(),
            transacao.getDescricao(),
            transacao.getValor(),
            transacao.getDataHora(),
            transacao.getSaldoApos(),
            transacao.getCategoria()
        );
    }

    private Long usuarioId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
