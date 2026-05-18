package com.banco.pagamento.adapters.outbound.persistence.repository;

import com.banco.pagamento.adapters.outbound.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByJtiAndRevogadoFalseAndExpiraEmAfter(String jti, LocalDateTime agora);

    Optional<RefreshTokenEntity> findByJti(String jti);
}
