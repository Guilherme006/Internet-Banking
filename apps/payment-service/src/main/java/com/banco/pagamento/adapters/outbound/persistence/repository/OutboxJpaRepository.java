package com.banco.pagamento.adapters.outbound.persistence.repository;

import com.banco.pagamento.adapters.outbound.persistence.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxJpaRepository extends JpaRepository<OutboxEntity, Long> {

    List<OutboxEntity> findTop50ByProcessadoFalseOrderByCriadoEmAsc();
}
