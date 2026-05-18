package com.banco.pagamento.adapters.outbound.persistence.adapter;

import com.banco.pagamento.adapters.outbound.persistence.entity.ContaEntity;
import com.banco.pagamento.adapters.outbound.persistence.mapper.ContaMapper;
import com.banco.pagamento.adapters.outbound.persistence.repository.ContaJpaRepository;
import com.banco.pagamento.application.domain.Conta;
import com.banco.pagamento.ports.outbound.ContaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContaRepositoryAdapter implements ContaRepositoryPort {

    private final ContaJpaRepository contaJpaRepository;
    private final ContaMapper contaMapper;

    @Override
    public Optional<Conta> buscarComLockPessimista(String numeroConta) {
        return contaJpaRepository.findByNumeroContaWithLock(numeroConta)
            .map(contaMapper::toDomain);
    }

    @Override
    public Conta salvar(Conta conta) {
        ContaEntity entity = contaMapper.toEntity(conta);
        ContaEntity saved = contaJpaRepository.save(entity);
        return contaMapper.toDomain(saved);
    }
}
