package com.banco.pagamento.adapters.outbound.persistence.repository;

import com.banco.pagamento.adapters.outbound.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Optional<UsuarioEntity> findByEmail(String email);
}
