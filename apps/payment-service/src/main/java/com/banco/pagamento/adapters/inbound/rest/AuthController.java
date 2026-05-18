package com.banco.pagamento.adapters.inbound.rest;

import com.banco.pagamento.adapters.inbound.rest.dto.AuthResponse;
import com.banco.pagamento.adapters.inbound.rest.dto.CadastroRequest;
import com.banco.pagamento.adapters.inbound.rest.dto.EnderecoRequest;
import com.banco.pagamento.adapters.inbound.rest.dto.LoginRequest;
import com.banco.pagamento.adapters.inbound.rest.dto.UsuarioResponse;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.usecase.AutenticacaoResultado;
import com.banco.pagamento.application.usecase.CadastroUsuarioComando;
import com.banco.pagamento.application.usecase.EnderecoComando;
import com.banco.pagamento.application.usecase.LoginComando;
import com.banco.pagamento.application.usecase.UsuarioResultado;
import com.banco.pagamento.ports.inbound.AutenticarUsuarioPort;
import com.banco.pagamento.ports.inbound.CadastrarUsuarioPort;
import com.banco.pagamento.ports.inbound.ConsultarUsuarioAutenticadoPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AutenticarUsuarioPort autenticarUsuarioPort;
    private final CadastrarUsuarioPort cadastrarUsuarioPort;
    private final ConsultarUsuarioAutenticadoPort consultarUsuarioAutenticadoPort;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return toResponse(autenticarUsuarioPort.autenticar(
            new LoginComando(request.email(), request.senha())
        ));
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse cadastrar(@Valid @RequestBody CadastroRequest request) {
        return toResponse(cadastrarUsuarioPort.cadastrar(
            new CadastroUsuarioComando(
                request.nome(),
                request.email(),
                request.cpf(),
                request.senha(),
                toComando(request.endereco())
            )
        ));
    }

    @GetMapping("/me")
    public UsuarioResponse me(Authentication authentication) {
        Usuario usuario = consultarUsuarioAutenticadoPort.consultar(usuarioId(authentication));
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getCpf(),
            "0001",
            usuario.getNumeroConta()
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

    private AuthResponse toResponse(AutenticacaoResultado resultado) {
        return new AuthResponse(
            resultado.token(),
            resultado.tipo(),
            resultado.expiraEmSegundos(),
            toResponse(resultado.usuario())
        );
    }

    private UsuarioResponse toResponse(UsuarioResultado usuario) {
        return new UsuarioResponse(
            usuario.id(),
            usuario.nome(),
            usuario.email(),
            usuario.cpf(),
            usuario.agencia(),
            usuario.conta()
        );
    }

    private Long usuarioId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
