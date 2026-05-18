package com.banco.pagamento.adapters.inbound.rest;

import com.banco.pagamento.adapters.inbound.rest.dto.BoletoResponse;
import com.banco.pagamento.adapters.inbound.rest.dto.PagamentoRequest;
import com.banco.pagamento.adapters.inbound.rest.dto.PagamentoResponse;
import com.banco.pagamento.adapters.security.AuditoriaService;
import com.banco.pagamento.application.domain.Boleto;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.usecase.PagamentoComando;
import com.banco.pagamento.application.usecase.PagamentoResultado;
import com.banco.pagamento.ports.inbound.ConsultarBoletoPort;
import com.banco.pagamento.ports.inbound.ConsultarUsuarioAutenticadoPort;
import com.banco.pagamento.ports.inbound.ProcessarPagamentoPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final ProcessarPagamentoPort processarPagamentoPort;
    private final ConsultarBoletoPort consultarBoletoPort;
    private final ConsultarUsuarioAutenticadoPort consultarUsuarioAutenticadoPort;
    private final AuditoriaService auditoriaService;

    @GetMapping("/boletos/{codigoBarra}")
    public BoletoResponse consultarBoleto(
            @PathVariable
            @Pattern(regexp = "\\d{44,48}", message = "Código de barras deve conter entre 44 e 48 dígitos")
            String codigoBarra) {
        return toResponse(consultarBoletoPort.consultar(codigoBarra));
    }

        @PostMapping("/boletos")
    public ResponseEntity<PagamentoResponse> pagarBoleto(
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody PagamentoRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest) {

        String chave = resolverChaveIdempotencia(idempotencyKey);
        Usuario usuario = consultarUsuarioAutenticadoPort.consultar(usuarioId(authentication));
        log.info("Iniciando pagamento | conta={} | boleto={} | idempotencyKey={}",
            usuario.getNumeroConta(), request.codigoBarra(), chave);

        PagamentoComando comando = new PagamentoComando(
            usuario.getNumeroConta(),
            request.codigoBarra(),
            chave
        );

        PagamentoResultado resultado = processarPagamentoPort.processar(comando);

        PagamentoResponse response = toResponse(resultado);
        HttpStatus status = resultado.reprocessado() ? HttpStatus.OK : HttpStatus.CREATED;

        log.info("Pagamento concluído | transacaoId={} | reprocessado={}",
            resultado.transacaoId(), resultado.reprocessado());
        auditoriaService.registrar(
            "PAGAMENTO_BOLETO",
            "SUCESSO",
            usuario.getId(),
            usuario.getEmail(),
            servletRequest,
            "Transacao " + resultado.transacaoId()
        );

        return ResponseEntity.status(status).body(response);
    }

    private String resolverChaveIdempotencia(String chave) {
        if (chave == null || chave.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return chave;
    }

    private PagamentoResponse toResponse(PagamentoResultado resultado) {
        return new PagamentoResponse(
            resultado.transacaoId(),
            resultado.numeroConta(),
            resultado.codigoBarra(),
            resultado.valorDebitado(),
            resultado.dataHora(),
            resultado.status(),
            resultado.reprocessado()
        );
    }

    private BoletoResponse toResponse(Boleto boleto) {
        return new BoletoResponse(
            boleto.getCodigoBarra(),
            boleto.getBeneficiario(),
            boleto.getPagador(),
            boleto.getValor(),
            boleto.getDataVencimento(),
            boleto.getStatus().name()
        );
    }

    private Long usuarioId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
