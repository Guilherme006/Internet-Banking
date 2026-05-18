package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.domain.exception.ContaNaoEncontradaException;
import com.banco.pagamento.application.domain.exception.CredenciaisInvalidasException;
import com.banco.pagamento.ports.inbound.AutenticarUsuarioPort;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.PasswordHasherPort;
import com.banco.pagamento.ports.outbound.UsuarioRepositoryPort;

public class AutenticarUsuarioUseCase implements AutenticarUsuarioPort {

    private final UsuarioRepositoryPort usuarioRepository;
    private final ContaRepositoryPort contaRepository;
    private final PasswordHasherPort passwordHasher;
    private final SessaoFactory sessaoFactory;

    public AutenticarUsuarioUseCase(
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
    public AutenticacaoResultado autenticar(LoginComando comando) {
        Usuario usuario = usuarioRepository.buscarPorEmail(normalizarEmail(comando.email()))
            .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordHasher.matches(comando.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        Conta conta = contaRepository.buscarPorNumero(usuario.getNumeroConta())
            .orElseThrow(() -> new ContaNaoEncontradaException(usuario.getNumeroConta()));

        return sessaoFactory.criar(usuario, conta);
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

}
