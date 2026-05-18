package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.application.domain.Endereco;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.domain.exception.UsuarioJaCadastradoException;
import com.banco.pagamento.ports.inbound.CadastrarUsuarioPort;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.PasswordHasherPort;
import com.banco.pagamento.ports.outbound.TokenPort;
import com.banco.pagamento.ports.outbound.UsuarioRepositoryPort;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class CadastrarUsuarioUseCase implements CadastrarUsuarioPort {

    private static final String AGENCIA_PADRAO = "0001";
    private static final BigDecimal SALDO_INICIAL = new BigDecimal("5000.00");
    private static final long EXPIRA_EM_SEGUNDOS = 3600;

    private final UsuarioRepositoryPort usuarioRepository;
    private final ContaRepositoryPort contaRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenPort tokenPort;

    public CadastrarUsuarioUseCase(
            UsuarioRepositoryPort usuarioRepository,
            ContaRepositoryPort contaRepository,
            PasswordHasherPort passwordHasher,
            TokenPort tokenPort) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
        this.passwordHasher = passwordHasher;
        this.tokenPort = tokenPort;
    }

    @Override
    public AutenticacaoResultado cadastrar(CadastroUsuarioComando comando) {
        String email = normalizarEmail(comando.email());
        String cpf = somenteDigitos(comando.cpf());

        if (usuarioRepository.existePorEmail(email)) {
            throw new UsuarioJaCadastradoException("e-mail");
        }
        if (usuarioRepository.existePorCpf(cpf)) {
            throw new UsuarioJaCadastradoException("CPF");
        }

        Conta conta = contaRepository.salvar(Conta.builder()
            .numeroConta(gerarNumeroConta())
            .agencia(AGENCIA_PADRAO)
            .titular(comando.nome().trim())
            .saldo(SALDO_INICIAL)
            .build());

        Usuario usuario = usuarioRepository.salvar(Usuario.builder()
            .nome(comando.nome().trim())
            .email(email)
            .cpf(cpf)
            .senhaHash(passwordHasher.hash(comando.senha()))
            .numeroConta(conta.getNumeroConta())
            .endereco(toEndereco(comando.endereco()))
            .build());

        return new AutenticacaoResultado(
            tokenPort.gerar(usuario),
            "Bearer",
            EXPIRA_EM_SEGUNDOS,
            new UsuarioResultado(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                conta.getAgencia(),
                conta.getNumeroConta()
            )
        );
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
