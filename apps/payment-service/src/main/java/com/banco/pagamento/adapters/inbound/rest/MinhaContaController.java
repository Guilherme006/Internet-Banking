package com.banco.pagamento.adapters.inbound.rest;

import com.banco.pagamento.adapters.inbound.rest.dto.AtualizarMinhaContaRequest;
import com.banco.pagamento.adapters.inbound.rest.dto.EnderecoRequest;
import com.banco.pagamento.adapters.inbound.rest.dto.MinhaContaResponse;
import com.banco.pagamento.adapters.security.AuditoriaService;
import com.banco.pagamento.application.usecase.AtualizarMinhaContaComando;
import com.banco.pagamento.application.usecase.EnderecoComando;
import com.banco.pagamento.application.usecase.MinhaContaResultado;
import com.banco.pagamento.ports.inbound.AtualizarMinhaContaPort;
import com.banco.pagamento.ports.inbound.ConsultarMinhaContaPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/minha-conta")
@RequiredArgsConstructor
public class MinhaContaController {

    private final ConsultarMinhaContaPort consultarMinhaContaPort;
    private final AtualizarMinhaContaPort atualizarMinhaContaPort;
    private final AuditoriaService auditoriaService;

    @GetMapping
    public MinhaContaResponse consultar(Authentication authentication) {
        return toResponse(consultarMinhaContaPort.consultar(usuarioId(authentication)));
    }

    @PutMapping
    public MinhaContaResponse atualizar(
            @Valid @RequestBody AtualizarMinhaContaRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest) {
        MinhaContaResultado resultado = atualizarMinhaContaPort.atualizar(new AtualizarMinhaContaComando(
            usuarioId(authentication),
            request.nome(),
            request.email(),
            toComando(request.endereco())
        ));
        auditoriaService.registrar(
            "ATUALIZACAO_CADASTRAL",
            "SUCESSO",
            resultado.id(),
            resultado.email(),
            servletRequest,
            "Dados cadastrais atualizados"
        );
        return toResponse(resultado);
    }

    private MinhaContaResponse toResponse(MinhaContaResultado resultado) {
        return new MinhaContaResponse(
            resultado.id(),
            resultado.nome(),
            resultado.email(),
            resultado.cpf(),
            resultado.agencia(),
            resultado.conta(),
            resultado.saldo(),
            toResponse(resultado.endereco())
        );
    }

    private EnderecoRequest toResponse(EnderecoComando endereco) {
        return new EnderecoRequest(
            endereco.cep(),
            endereco.logradouro(),
            endereco.numero(),
            endereco.complemento(),
            endereco.bairro(),
            endereco.cidade(),
            endereco.uf()
        );
    }

    private EnderecoComando toComando(EnderecoRequest endereco) {
        return new EnderecoComando(
            endereco.cep(),
            endereco.logradouro(),
            endereco.numero(),
            endereco.complemento(),
            endereco.bairro(),
            endereco.cidade(),
            endereco.uf()
        );
    }

    private Long usuarioId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
