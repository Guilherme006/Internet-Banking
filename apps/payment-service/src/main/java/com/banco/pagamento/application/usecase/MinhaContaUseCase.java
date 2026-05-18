package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.application.domain.Endereco;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.domain.exception.CadastroInvalidoException;
import com.banco.pagamento.application.domain.exception.ContaNaoEncontradaException;
import com.banco.pagamento.application.domain.exception.UsuarioJaCadastradoException;
import com.banco.pagamento.application.domain.exception.UsuarioNaoEncontradoException;
import com.banco.pagamento.ports.inbound.AtualizarMinhaContaPort;
import com.banco.pagamento.ports.inbound.ConsultarMinhaContaPort;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.UsuarioRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

public class MinhaContaUseCase implements ConsultarMinhaContaPort, AtualizarMinhaContaPort {

    private final UsuarioRepositoryPort usuarioRepository;
    private final ContaRepositoryPort contaRepository;

    public MinhaContaUseCase(UsuarioRepositoryPort usuarioRepository, ContaRepositoryPort contaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
    }

    @Override
    public MinhaContaResultado consultar(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Conta conta = buscarConta(usuario);
        return toResultado(usuario, conta);
    }

    @Override
    @Transactional
    public MinhaContaResultado atualizar(AtualizarMinhaContaComando comando) {
        Usuario usuario = buscarUsuario(comando.usuarioId());
        Conta conta = buscarConta(usuario);
        String email = normalizarEmail(comando.email());
        validarAtualizacao(comando, email);

        usuarioRepository.buscarPorEmail(email)
            .filter(encontrado -> !encontrado.getId().equals(usuario.getId()))
            .ifPresent(encontrado -> {
                throw new UsuarioJaCadastradoException("e-mail");
            });

        Usuario atualizado = usuarioRepository.salvar(Usuario.builder()
            .id(usuario.getId())
            .nome(comando.nome().trim())
            .email(email)
            .cpf(usuario.getCpf())
            .senhaHash(usuario.getSenhaHash())
            .numeroConta(usuario.getNumeroConta())
            .endereco(toEndereco(comando.endereco()))
            .build());

        return toResultado(atualizado, conta);
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.buscarPorId(usuarioId)
            .orElseThrow(UsuarioNaoEncontradoException::new);
    }

    private Conta buscarConta(Usuario usuario) {
        return contaRepository.buscarPorNumero(usuario.getNumeroConta())
            .orElseThrow(() -> new ContaNaoEncontradaException(usuario.getNumeroConta()));
    }

    private void validarAtualizacao(AtualizarMinhaContaComando comando, String email) {
        if (comando.nome() == null || comando.nome().trim().length() < 3) {
            throw new CadastroInvalidoException("Nome deve ter pelo menos 3 caracteres.");
        }
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new CadastroInvalidoException("E-mail inválido.");
        }
        EnderecoComando endereco = comando.endereco();
        if (endereco == null || somenteDigitos(endereco.cep()).length() != 8) {
            throw new CadastroInvalidoException("CEP inválido.");
        }
        if (endereco.uf() == null || !endereco.uf().trim().matches("(?i)^[A-Z]{2}$")) {
            throw new CadastroInvalidoException("UF inválida.");
        }
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

    private MinhaContaResultado toResultado(Usuario usuario, Conta conta) {
        Endereco endereco = usuario.getEndereco();
        return new MinhaContaResultado(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getCpf(),
            conta.getAgencia(),
            conta.getNumeroConta(),
            conta.getSaldo(),
            new EnderecoComando(
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf()
            )
        );
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }
}
