package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.domain.exception.CadastroInvalidoException;
import com.banco.pagamento.application.domain.exception.UsuarioJaCadastradoException;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.PasswordHasherPort;
import com.banco.pagamento.ports.outbound.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CadastrarUsuarioUseCase")
class CadastrarUsuarioUseCaseTest {

    @Mock private UsuarioRepositoryPort usuarioRepository;
    @Mock private ContaRepositoryPort contaRepository;
    @Mock private PasswordHasherPort passwordHasher;
    @Mock private SessaoFactory sessaoFactory;

    private CadastrarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CadastrarUsuarioUseCase(usuarioRepository, contaRepository, passwordHasher, sessaoFactory);
    }

    @Test
    @DisplayName("deve cadastrar usuário com CPF/CEP válidos, senha forte e conta única")
    void deveCadastrarUsuario() {
        CadastroUsuarioComando comando = comandoValido();
        Conta contaSalva = conta("45678-8");
        Usuario usuarioSalvo = usuario("45678-8");
        AutenticacaoResultado sessao = sessao(usuarioSalvo);
        when(usuarioRepository.existePorEmail("maria@empresa.com")).thenReturn(false);
        when(usuarioRepository.existePorCpf("52998224725")).thenReturn(false);
        when(contaRepository.existePorNumero(any())).thenReturn(false);
        when(contaRepository.salvar(any())).thenReturn(contaSalva);
        when(passwordHasher.hash("Senha@123")).thenReturn("hash");
        when(usuarioRepository.salvar(any())).thenReturn(usuarioSalvo);
        when(sessaoFactory.criar(usuarioSalvo, contaSalva)).thenReturn(sessao);

        AutenticacaoResultado resultado = useCase.cadastrar(comando);

        assertThat(resultado.usuario().email()).isEqualTo("maria@empresa.com");
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).salvar(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue().getCpf()).isEqualTo("52998224725");
        assertThat(usuarioCaptor.getValue().getEndereco().getCep()).isEqualTo("01001000");
        assertThat(usuarioCaptor.getValue().getEndereco().getUf()).isEqualTo("SP");
    }

    @Test
    @DisplayName("deve rejeitar CPF inválido")
    void deveRejeitarCpfInvalido() {
        CadastroUsuarioComando comando = new CadastroUsuarioComando(
            "Maria", "maria@empresa.com", "11111111111", "Senha@123", enderecoValido()
        );

        assertThatThrownBy(() -> useCase.cadastrar(comando))
            .isInstanceOf(CadastroInvalidoException.class)
            .hasMessageContaining("CPF");
    }

    @Test
    @DisplayName("deve rejeitar senha fraca")
    void deveRejeitarSenhaFraca() {
        CadastroUsuarioComando comando = new CadastroUsuarioComando(
            "Maria", "maria@empresa.com", "52998224725", "senha123", enderecoValido()
        );

        assertThatThrownBy(() -> useCase.cadastrar(comando))
            .isInstanceOf(CadastroInvalidoException.class)
            .hasMessageContaining("senha");
    }

    @Test
    @DisplayName("deve rejeitar CEP inválido")
    void deveRejeitarCepInvalido() {
        EnderecoComando endereco = new EnderecoComando("123", "Praça da Sé", "100", "", "Sé", "São Paulo", "SP");
        CadastroUsuarioComando comando = new CadastroUsuarioComando(
            "Maria", "maria@empresa.com", "52998224725", "Senha@123", endereco
        );

        assertThatThrownBy(() -> useCase.cadastrar(comando))
            .isInstanceOf(CadastroInvalidoException.class)
            .hasMessageContaining("CEP");
    }

    @Test
    @DisplayName("deve rejeitar e-mail já cadastrado")
    void deveRejeitarEmailDuplicado() {
        when(usuarioRepository.existePorEmail("maria@empresa.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.cadastrar(comandoValido()))
            .isInstanceOf(UsuarioJaCadastradoException.class);
    }

    @Test
    @DisplayName("deve rejeitar CPF já cadastrado")
    void deveRejeitarCpfDuplicado() {
        when(usuarioRepository.existePorEmail("maria@empresa.com")).thenReturn(false);
        when(usuarioRepository.existePorCpf("52998224725")).thenReturn(true);

        assertThatThrownBy(() -> useCase.cadastrar(comandoValido()))
            .isInstanceOf(UsuarioJaCadastradoException.class);
    }

    private CadastroUsuarioComando comandoValido() {
        return new CadastroUsuarioComando(
            "Maria Souza",
            "  Maria@Empresa.com ",
            "529.982.247-25",
            "Senha@123",
            enderecoValido()
        );
    }

    private EnderecoComando enderecoValido() {
        return new EnderecoComando("01001-000", "Praça da Sé", "100", "", "Sé", "São Paulo", "sp");
    }

    private Conta conta(String numeroConta) {
        return Conta.builder()
            .id(2L)
            .numeroConta(numeroConta)
            .agencia("0001")
            .titular("Maria Souza")
            .saldo(new BigDecimal("5000.00"))
            .versao(0L)
            .build();
    }

    private Usuario usuario(String numeroConta) {
        return Usuario.builder()
            .id(2L)
            .nome("Maria Souza")
            .email("maria@empresa.com")
            .cpf("52998224725")
            .senhaHash("hash")
            .numeroConta(numeroConta)
            .build();
    }

    private AutenticacaoResultado sessao(Usuario usuario) {
        return new AutenticacaoResultado(
            "access",
            "refresh",
            "Bearer",
            900,
            604800,
            new UsuarioResultado(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getCpf(), "0001", usuario.getNumeroConta())
        );
    }
}
