package com.banco.pagamento.application.usecase;

import com.banco.pagamento.ports.inbound.EncerrarSessaoPort;
import com.banco.pagamento.ports.outbound.RefreshTokenRepositoryPort;
import com.banco.pagamento.ports.outbound.TokenPort;

public class EncerrarSessaoUseCase implements EncerrarSessaoPort {

    private final TokenPort tokenPort;
    private final RefreshTokenRepositoryPort refreshTokenRepository;

    public EncerrarSessaoUseCase(TokenPort tokenPort, RefreshTokenRepositoryPort refreshTokenRepository) {
        this.tokenPort = tokenPort;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void encerrar(SessaoComando comando) {
        tokenPort.extrairRefreshToken(comando.refreshToken())
            .ifPresent(claims -> refreshTokenRepository.revogar(claims.jti()));
    }
}
