package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.application.domain.exception.ContaNaoEncontradaException;
import com.banco.pagamento.application.domain.exception.CredenciaisInvalidasException;
import com.banco.pagamento.ports.inbound.AutenticarUsuarioPort;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import com.banco.pagamento.ports.outbound.PasswordHasherPort;
import com.banco.pagamento.ports.outbound.TokenPort;
import com.banco.pagamento.ports.outbound.UsuarioRepositoryPort;

public class AutenticarUsuarioUseCase implements AutenticarUsuarioPort {

    private static final long EXPIRA_EM_SEGUNDOS = 3600;

    private final UsuarioRepositoryPort usuarioRepository;
    private final ContaRepositoryPort contaRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenPort tokenPort;

    public AutenticarUsuarioUseCase(
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
    public AutenticacaoResultado autenticar(LoginComando comando) {
        Usuario usuario = usuarioRepository.buscarPorEmail(normalizarEmail(comando.email()))
            .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordHasher.matches(comando.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        Conta conta = contaRepository.buscarPorNumero(usuario.getNumeroConta())
            .orElseThrow(() -> new ContaNaoEncontradaException(usuario.getNumeroConta()));

        return new AutenticacaoResultado(
            tokenPort.gerar(usuario),
            "Bearer",
            EXPIRA_EM_SEGUNDOS,
            toResultado(usuario, conta)
        );
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private UsuarioResultado toResultado(Usuario usuario, Conta conta) {
        return new UsuarioResultado(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getCpf(),
            conta.getAgencia(),
            conta.getNumeroConta()
        );
    }
}
