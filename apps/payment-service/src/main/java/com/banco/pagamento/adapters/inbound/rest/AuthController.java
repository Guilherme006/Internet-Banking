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
import com.banco.pagamento.application.usecase.SessaoComando;
import com.banco.pagamento.application.usecase.UsuarioResultado;
import com.banco.pagamento.ports.inbound.AutenticarUsuarioPort;
import com.banco.pagamento.ports.inbound.CadastrarUsuarioPort;
import com.banco.pagamento.ports.inbound.ConsultarUsuarioAutenticadoPort;
import com.banco.pagamento.ports.inbound.EncerrarSessaoPort;
import com.banco.pagamento.ports.inbound.RenovarSessaoPort;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

    private static final String ACCESS_COOKIE = "BP_ACCESS_TOKEN";
    private static final String REFRESH_COOKIE = "BP_REFRESH_TOKEN";

    private final AutenticarUsuarioPort autenticarUsuarioPort;
    private final CadastrarUsuarioPort cadastrarUsuarioPort;
    private final ConsultarUsuarioAutenticadoPort consultarUsuarioAutenticadoPort;
    private final RenovarSessaoPort renovarSessaoPort;
    private final EncerrarSessaoPort encerrarSessaoPort;

    @Value("${security.cookies.secure}")
    private boolean secureCookie;

    @Value("${security.cookies.same-site}")
    private String sameSite;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AutenticacaoResultado resultado = autenticarUsuarioPort.autenticar(
            new LoginComando(request.email(), request.senha())
        );
        aplicarCookies(response, resultado);
        return toResponse(resultado);
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse cadastrar(@Valid @RequestBody CadastroRequest request, HttpServletResponse response) {
        AutenticacaoResultado resultado = cadastrarUsuarioPort.cadastrar(
            new CadastroUsuarioComando(
                request.nome(),
                request.email(),
                request.cpf(),
                request.senha(),
                toComando(request.endereco())
            )
        );
        aplicarCookies(response, resultado);
        return toResponse(resultado);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        AutenticacaoResultado resultado = renovarSessaoPort.renovar(
            new SessaoComando(cookie(request, REFRESH_COOKIE))
        );
        aplicarCookies(response, resultado);
        return toResponse(resultado);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        encerrarSessaoPort.encerrar(new SessaoComando(cookie(request, REFRESH_COOKIE)));
        limparCookies(response);
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
            null,
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

    private void aplicarCookies(HttpServletResponse response, AutenticacaoResultado resultado) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(ACCESS_COOKIE, resultado.accessToken(), resultado.expiraEmSegundos(), "/").toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(REFRESH_COOKIE, resultado.refreshToken(), resultado.refreshExpiraEmSegundos(), "/api/v1/auth").toString());
    }

    private void limparCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(ACCESS_COOKIE, "", 0, "/").toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(REFRESH_COOKIE, "", 0, "/api/v1/auth").toString());
    }

    private ResponseCookie cookie(String nome, String valor, long maxAge, String path) {
        return ResponseCookie.from(nome, valor == null ? "" : valor)
            .httpOnly(true)
            .secure(secureCookie)
            .sameSite(sameSite)
            .path(path)
            .maxAge(maxAge)
            .build();
    }

    private String cookie(HttpServletRequest request, String nome) {
        if (request.getCookies() == null) {
            return "";
        }
        for (Cookie cookie : request.getCookies()) {
            if (nome.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return "";
    }
}
