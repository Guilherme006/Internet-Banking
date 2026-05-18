package com.banco.pagamento.adapters.outbound.persistence.repository;

import com.banco.pagamento.adapters.outbound.persistence.entity.IdempotenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IdempotenciaJpaRepository extends JpaRepository<IdempotenciaEntity, Long> {

    Optional<IdempotenciaEntity> findByChaveAndExpiraEmAfter(String chave, LocalDateTime agora);

    @Modifying
    @Query("DELETE FROM IdempotenciaEntity i WHERE i.expiraEm < :agora")
    void deleteExpired(LocalDateTime agora);
}
