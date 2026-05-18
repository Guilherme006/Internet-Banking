package com.banco.pagamento.adapters.outbound.persistence.repository;

import com.banco.pagamento.adapters.outbound.persistence.entity.AuditoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaJpaRepository extends JpaRepository<AuditoriaEntity, Long> {
}
