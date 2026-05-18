package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.application.domain.Endereco;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.domain.exception.CadastroInvalidoException;
import com.banco.pagamento.application.domain.exception.UsuarioJaCadastradoException;
import com.banco.pagamento.ports.inbound.CadastrarUsuarioPort;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.PasswordHasherPort;
import com.banco.pagamento.ports.outbound.UsuarioRepositoryPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class CadastrarUsuarioUseCase implements CadastrarUsuarioPort {

    private static final String AGENCIA_PADRAO = "0001";
    private static final BigDecimal SALDO_INICIAL = new BigDecimal("5000.00");
    private static final int MAX_TENTATIVAS_CONTA = 20;

    private final UsuarioRepositoryPort usuarioRepository;
    private final ContaRepositoryPort contaRepository;
    private final PasswordHasherPort passwordHasher;
    private final SessaoFactory sessaoFactory;

    public CadastrarUsuarioUseCase(
            UsuarioRepositoryPort usuarioRepository,
            ContaRepositoryPort contaRepository,
            PasswordHasherPort passwordHasher,
            SessaoFactory sessaoFactory) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
        this.passwordHasher = passwordHasher;
        this.sessaoFactory = sessaoFactory;
    }

    @Override
    @Transactional
    public AutenticacaoResultado cadastrar(CadastroUsuarioComando comando) {
        String email = normalizarEmail(comando.email());
        String cpf = somenteDigitos(comando.cpf());
        validarCadastro(comando, cpf);

        if (usuarioRepository.existePorEmail(email)) {
            throw new UsuarioJaCadastradoException("e-mail");
        }
        if (usuarioRepository.existePorCpf(cpf)) {
            throw new UsuarioJaCadastradoException("CPF");
        }

        Conta conta = criarConta(comando.nome());

        Usuario usuario = usuarioRepository.salvar(Usuario.builder()
            .nome(comando.nome().trim())
            .email(email)
            .cpf(cpf)
            .senhaHash(passwordHasher.hash(comando.senha()))
            .numeroConta(conta.getNumeroConta())
            .endereco(toEndereco(comando.endereco()))
            .build());

        return sessaoFactory.criar(usuario, conta);
    }

    private Endereco toEndereco(EnderecoComando endereco) {
        return Endereco.builder()
            .cep(somenteDigitos(endereco.cep()))
            .logradouro(endereco.logradouro().trim())
            .numero(endereco.numero().trim())
            .complemento(endereco.complemento() == null ? null : endereco.complemento().trim())
            .bairro(endereco.bairro().trim())
            .cidade(endereco.cidade().trim())
            .uf(endereco.uf().trim().toUpperCase())
            .build();
    }

    private void validarCadastro(CadastroUsuarioComando comando, String cpf) {
        if (!cpfValido(cpf)) {
            throw new CadastroInvalidoException("CPF inválido.");
        }

        if (comando.senha() == null || !comando.senha().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,72}$")) {
            throw new CadastroInvalidoException("A senha deve ter entre 8 e 72 caracteres, com maiúscula, minúscula, número e símbolo.");
        }

        EnderecoComando endereco = comando.endereco();
        String cep = somenteDigitos(endereco.cep());
        if (cep.length() != 8) {
            throw new CadastroInvalidoException("CEP inválido.");
        }
        if (endereco.uf() == null || !endereco.uf().trim().matches("(?i)^[A-Z]{2}$")) {
            throw new CadastroInvalidoException("UF inválida.");
        }
    }

    private boolean cpfValido(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }

        return calcularDigito(cpf, 9) == Character.digit(cpf.charAt(9), 10)
            && calcularDigito(cpf, 10) == Character.digit(cpf.charAt(10), 10);
    }

    private int calcularDigito(String cpf, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += Character.digit(cpf.charAt(i), 10) * (tamanho + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private Conta criarConta(String titular) {
        for (int tentativa = 0; tentativa < MAX_TENTATIVAS_CONTA; tentativa++) {
            String numeroConta = gerarNumeroConta();
            if (!contaRepository.existePorNumero(numeroConta)) {
                try {
                    return contaRepository.salvar(Conta.builder()
                        .numeroConta(numeroConta)
                        .agencia(AGENCIA_PADRAO)
                        .titular(titular.trim())
                        .saldo(SALDO_INICIAL)
                        .build());
                } catch (DataIntegrityViolationException ex) {
                    continue;
                }
            }
        }
        throw new CadastroInvalidoException("Não foi possível gerar uma conta. Tente novamente.");
    }

    private String gerarNumeroConta() {
        int base = ThreadLocalRandom.current().nextInt(10000, 100000);
        int digito = base % 10;
        return base + "-" + digito;
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }
}
