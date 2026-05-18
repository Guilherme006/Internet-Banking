package com.banco.pagamento.adapters.outbound.persistence.repository;

import com.banco.pagamento.adapters.outbound.persistence.entity.TransacaoEntity;
import com.banco.pagamento.application.domain.TipoTransacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransacaoJpaRepository extends JpaRepository<TransacaoEntity, String> {

    @Query("""
        SELECT t
        FROM TransacaoEntity t
        WHERE t.numeroConta = :numeroConta
          AND (:tipo IS NULL OR t.tipo = :tipo)
          AND (:dataInicio IS NULL OR t.dataHora >= :dataInicio)
          AND (:dataFim IS NULL OR t.dataHora <= :dataFim)
        ORDER BY t.dataHora DESC
        """)
    Page<TransacaoEntity> buscarExtrato(
        @Param("numeroConta") String numeroConta,
        @Param("tipo") TipoTransacao tipo,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim,
        Pageable pageable
    );
}
