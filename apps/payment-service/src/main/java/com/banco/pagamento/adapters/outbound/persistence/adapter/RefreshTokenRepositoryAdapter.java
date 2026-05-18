package com.banco.pagamento.adapters.outbound.persistence.adapter;

import com.banco.pagamento.adapters.outbound.persistence.entity.RefreshTokenEntity;
import com.banco.pagamento.adapters.outbound.persistence.repository.RefreshTokenJpaRepository;
import com.banco.pagamento.application.domain.RefreshToken;
import com.banco.pagamento.ports.outbound.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository repository;

    @Override
    public RefreshToken salvar(RefreshToken refreshToken) {
        RefreshTokenEntity saved = repository.save(RefreshTokenEntity.builder()
            .jti(refreshToken.getJti())
            .usuarioId(refreshToken.getUsuarioId())
            .expiraEm(refreshToken.getExpiraEm())
            .revogado(refreshToken.isRevogado())
            .criadoEm(LocalDateTime.now())
            .build());
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> buscarAtivoPorJti(String jti) {
        return repository.findByJtiAndRevogadoFalseAndExpiraEmAfter(jti, LocalDateTime.now())
            .map(this::toDomain);
    }

    @Override
    public void revogar(String jti) {
        repository.findByJti(jti).ifPresent(entity -> {
            entity.setRevogado(true);
            entity.setRevogadoEm(LocalDateTime.now());
            repository.save(entity);
        });
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.builder()
            .id(entity.getId())
            .jti(entity.getJti())
            .usuarioId(entity.getUsuarioId())
            .expiraEm(entity.getExpiraEm())
            .revogado(entity.isRevogado())
            .build();
    }
}
