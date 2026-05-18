package com.banco.pagamento.adapters.inbound.rest;

import com.banco.pagamento.adapters.inbound.rest.dto.CadastroRequest;
import com.banco.pagamento.adapters.inbound.rest.dto.EnderecoRequest;
import com.banco.pagamento.adapters.inbound.rest.dto.LoginRequest;
import com.banco.pagamento.adapters.security.LoginRateLimiter;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.domain.exception.MuitasTentativasLoginException;
import com.banco.pagamento.application.usecase.AutenticacaoResultado;
import com.banco.pagamento.application.usecase.UsuarioResultado;
import com.banco.pagamento.ports.inbound.AutenticarUsuarioPort;
import com.banco.pagamento.ports.inbound.CadastrarUsuarioPort;
import com.banco.pagamento.ports.inbound.ConsultarUsuarioAutenticadoPort;
import com.banco.pagamento.ports.inbound.EncerrarSessaoPort;
import com.banco.pagamento.ports.inbound.RenovarSessaoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Mock private AutenticarUsuarioPort autenticarUsuarioPort;
    @Mock private CadastrarUsuarioPort cadastrarUsuarioPort;
    @Mock private ConsultarUsuarioAutenticadoPort consultarUsuarioAutenticadoPort;
    @Mock private RenovarSessaoPort renovarSessaoPort;
    @Mock private EncerrarSessaoPort encerrarSessaoPort;
    @Mock private LoginRateLimiter loginRateLimiter;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(
            autenticarUsuarioPort,
            cadastrarUsuarioPort,
            consultarUsuarioAutenticadoPort,
            renovarSessaoPort,
            encerrarSessaoPort,
            loginRateLimiter
        );
        ReflectionTestUtils.setField(controller, "secureCookie", false);
        ReflectionTestUtils.setField(controller, "sameSite", "Strict");
    }

    @Test
    @DisplayName("POST /auth/login deve criar cookies HttpOnly e não expor token no corpo")
    void loginDeveCriarCookiesHttpOnly() {
        when(loginRateLimiter.permite("joao@bancopagamento.com", "127.0.0.1")).thenReturn(true);
        when(autenticarUsuarioPort.autenticar(any())).thenReturn(sessao());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        var body = controller.login(new LoginRequest("joao@bancopagamento.com", "Senha@123"), request, response);

        assertThat(body.token()).isNull();
        assertThat(body.usuario().conta()).isEqualTo("12345-6");
        assertThat(response.getHeaders("Set-Cookie"))
            .anyMatch(cookie -> cookie.contains("BP_ACCESS_TOKEN=access") && cookie.contains("HttpOnly"))
            .anyMatch(cookie -> cookie.contains("BP_REFRESH_TOKEN=refresh") && cookie.contains("HttpOnly"));
    }

    @Test
    @DisplayName("POST /auth/login deve bloquear excesso de tentativas")
    void loginDeveBloquearExcessoDeTentativas() {
        when(loginRateLimiter.permite("joao@bancopagamento.com", "127.0.0.1")).thenReturn(false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThatThrownBy(() -> controller.login(
            new LoginRequest("joao@bancopagamento.com", "Senha@123"),
            request,
            response
        )).isInstanceOf(MuitasTentativasLoginException.class);
    }

    @Test
    @DisplayName("POST /auth/cadastro deve enviar dados normalizados ao use case")
    void cadastroDeveDelegarComEndereco() {
        when(cadastrarUsuarioPort.cadastrar(any())).thenReturn(sessao());
        MockHttpServletResponse response = new MockHttpServletResponse();
        CadastroRequest request = new CadastroRequest(
            "Maria Souza",
            "maria@empresa.com",
            "52998224725",
            "Senha@123",
            new EnderecoRequest("01001000", "Praça da Sé", "100", "", "Sé", "São Paulo", "SP")
        );

        controller.cadastrar(request, response);

        ArgumentCaptor<com.banco.pagamento.application.usecase.CadastroUsuarioComando> captor =
            ArgumentCaptor.forClass(com.banco.pagamento.application.usecase.CadastroUsuarioComando.class);
        verify(cadastrarUsuarioPort).cadastrar(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("maria@empresa.com");
        assertThat(captor.getValue().endereco().cep()).isEqualTo("01001000");
    }

    @Test
    @DisplayName("GET /auth/me deve consultar usuário autenticado pelo principal")
    void meDeveUsarPrincipalAutenticado() {
        when(consultarUsuarioAutenticadoPort.consultar(10L)).thenReturn(usuario());

        var response = controller.me(new UsernamePasswordAuthenticationToken(10L, null, List.of()));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.conta()).isEqualTo("98765-4");
        verify(consultarUsuarioAutenticadoPort).consultar(10L);
    }

    private AutenticacaoResultado sessao() {
        return new AutenticacaoResultado(
            "access",
            "refresh",
            "Bearer",
            900,
            604800,
            new UsuarioResultado(1L, "João da Silva", "joao@bancopagamento.com", "52998224725", "0001", "12345-6")
        );
    }

    private Usuario usuario() {
        return Usuario.builder()
            .id(10L)
            .nome("Ana Silva")
            .email("ana@empresa.com")
            .cpf("15350946056")
            .numeroConta("98765-4")
            .build();
    }
}
