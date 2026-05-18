package com.banco.pagamento.application.usecase;

import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.application.domain.RefreshToken;
import com.banco.pagamento.application.domain.Usuario;
import com.banco.pagamento.ports.outbound.RefreshTokenRepositoryPort;
import com.banco.pagamento.ports.outbound.TokenPort;

import java.time.LocalDateTime;
import java.util.UUID;

public class SessaoFactory {

    private final TokenPort tokenPort;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final long accessExpiraEmSegundos;
    private final long refreshExpiraEmSegundos;

    public SessaoFactory(
            TokenPort tokenPort,
            RefreshTokenRepositoryPort refreshTokenRepository,
            long accessExpiraEmSegundos,
            long refreshExpiraEmSegundos) {
        this.tokenPort = tokenPort;
        this.refreshTokenRepository = refreshTokenRepository;
        this.accessExpiraEmSegundos = accessExpiraEmSegundos;
        this.refreshExpiraEmSegundos = refreshExpiraEmSegundos;
    }

    public AutenticacaoResultado criar(Usuario usuario, Conta conta) {
        String jti = UUID.randomUUID().toString();
        refreshTokenRepository.salvar(RefreshToken.builder()
            .jti(jti)
            .usuarioId(usuario.getId())
            .expiraEm(LocalDateTime.now().plusSeconds(refreshExpiraEmSegundos))
            .revogado(false)
            .build());

        return new AutenticacaoResultado(
            tokenPort.gerarAccessToken(usuario),
            tokenPort.gerarRefreshToken(usuario, jti),
            "Bearer",
            accessExpiraEmSegundos,
            refreshExpiraEmSegundos,
            toResultado(usuario, conta)
        );
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
