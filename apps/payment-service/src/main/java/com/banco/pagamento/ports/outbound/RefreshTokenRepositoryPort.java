package com.banco.pagamento.ports.outbound;

import com.banco.pagamento.application.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {

    RefreshToken salvar(RefreshToken refreshToken);

    Optional<RefreshToken> buscarAtivoPorJti(String jti);

    void revogar(String jti);
}
