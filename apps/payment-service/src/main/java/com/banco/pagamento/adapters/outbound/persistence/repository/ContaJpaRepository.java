package com.banco.pagamento.adapters.outbound.persistence.repository;

import com.banco.pagamento.adapters.outbound.persistence.entity.ContaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaJpaRepository extends JpaRepository<ContaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ContaEntity c WHERE c.numeroConta = :numeroConta")
    Optional<ContaEntity> findByNumeroContaWithLock(@Param("numeroConta") String numeroConta);

    Optional<ContaEntity> findByNumeroConta(String numeroConta);

    boolean existsByNumeroConta(String numeroConta);
}
