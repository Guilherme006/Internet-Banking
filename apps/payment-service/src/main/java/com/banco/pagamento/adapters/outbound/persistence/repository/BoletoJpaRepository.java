package com.banco.pagamento.adapters.outbound.persistence.repository;

import com.banco.pagamento.adapters.outbound.persistence.entity.BoletoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoletoJpaRepository extends JpaRepository<BoletoEntity, Long> {

    Optional<BoletoEntity> findByCodigoBarra(String codigoBarra);
}
