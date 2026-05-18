package com.banco.pagamento.adapters.inbound.rest;

import com.banco.pagamento.adapters.inbound.rest.dto.ExtratoResponse;
import com.banco.pagamento.adapters.inbound.rest.dto.TransacaoResponse;
import com.banco.pagamento.application.domain.TipoTransacao;
import com.banco.pagamento.application.domain.Transacao;
import com.banco.pagamento.application.usecase.ConsultarExtratoQuery;
import com.banco.pagamento.application.usecase.ExtratoResultado;
import com.banco.pagamento.ports.inbound.ConsultarExtratoPort;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

    private static final String CONTA_DEMO = "12345-6";

    private final ConsultarExtratoPort consultarExtratoPort;

    @GetMapping
    public ExtratoResponse consultar(
            @RequestParam(value = "numeroConta", defaultValue = CONTA_DEMO)
            @Pattern(regexp = "\\d{5}-\\d", message = "Formato inválido. Use: 12345-6")
            String numeroConta,
            @RequestParam(value = "tipo", required = false) TipoTransacao tipo,
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime dataInicio,
            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime dataFim,
            @RequestParam(value = "pagina", defaultValue = "0") @Min(0) int pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") @Min(1) @Max(100) int tamanho) {

        ConsultarExtratoQuery query = new ConsultarExtratoQuery(
            numeroConta,
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
}
