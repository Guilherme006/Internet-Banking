package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.domain.exception.ContaNaoEncontradaException;
import com.banco.pagamento.application.domain.exception.CredenciaisInvalidasException;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.PasswordHasherPort;
import com.banco.pagamento.ports.outbound.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AutenticarUsuarioUseCase")
class AutenticarUsuarioUseCaseTest {

    @Mock private UsuarioRepositoryPort usuarioRepository;
    @Mock private ContaRepositoryPort contaRepository;
    @Mock private PasswordHasherPort passwordHasher;
    @Mock private SessaoFactory sessaoFactory;

    private AutenticarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AutenticarUsuarioUseCase(usuarioRepository, contaRepository, passwordHasher, sessaoFactory);
    }

    @Test
    @DisplayName("deve autenticar normalizando e-mail e criar sessão")
    void deveAutenticarUsuario() {
        Usuario usuario = usuario();
        Conta conta = conta();
        AutenticacaoResultado sessao = sessao(usuario);
        when(usuarioRepository.buscarPorEmail("joao@bancopagamento.com")).thenReturn(Optional.of(usuario));
        when(passwordHasher.matches("Senha@123", "hash")).thenReturn(true);
        when(contaRepository.buscarPorNumero("12345-6")).thenReturn(Optional.of(conta));
        when(sessaoFactory.criar(usuario, conta)).thenReturn(sessao);

        AutenticacaoResultado resultado = useCase.autenticar(
            new LoginComando("  Joao@BancoPagamento.com ", "Senha@123")
        );

        assertThat(resultado.usuario().conta()).isEqualTo("12345-6");
        verify(sessaoFactory).criar(usuario, conta);
    }

    @Test
    @DisplayName("deve rejeitar e-mail inexistente sem revelar detalhe")
    void deveRejeitarEmailInexistente() {
        when(usuarioRepository.buscarPorEmail("naoexiste@banco.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.autenticar(new LoginComando("naoexiste@banco.com", "Senha@123")))
            .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    @DisplayName("deve rejeitar senha inválida")
    void deveRejeitarSenhaInvalida() {
        Usuario usuario = usuario();
        when(usuarioRepository.buscarPorEmail("joao@bancopagamento.com")).thenReturn(Optional.of(usuario));
        when(passwordHasher.matches("errada", "hash")).thenReturn(false);

        assertThatThrownBy(() -> useCase.autenticar(new LoginComando("joao@bancopagamento.com", "errada")))
            .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    @DisplayName("deve falhar quando a conta vinculada não existir")
    void deveFalharQuandoContaNaoExistir() {
        Usuario usuario = usuario();
        when(usuarioRepository.buscarPorEmail("joao@bancopagamento.com")).thenReturn(Optional.of(usuario));
        when(passwordHasher.matches("Senha@123", "hash")).thenReturn(true);
        when(contaRepository.buscarPorNumero("12345-6")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.autenticar(new LoginComando("joao@bancopagamento.com", "Senha@123")))
            .isInstanceOf(ContaNaoEncontradaException.class);
    }

    private Usuario usuario() {
        return Usuario.builder()
            .id(1L)
            .nome("João da Silva")
            .email("joao@bancopagamento.com")
            .cpf("52998224725")
            .senhaHash("hash")
            .numeroConta("12345-6")
            .build();
    }

    private Conta conta() {
        return Conta.builder()
            .id(1L)
            .numeroConta("12345-6")
            .agencia("0001")
            .titular("João da Silva")
            .saldo(new BigDecimal("5000.00"))
            .versao(0L)
            .build();
    }

    private AutenticacaoResultado sessao(Usuario usuario) {
        return new AutenticacaoResultado(
            "access",
            "refresh",
            "Bearer",
            900,
            604800,
            new UsuarioResultado(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getCpf(), "0001", "12345-6")
        );
    }
}
