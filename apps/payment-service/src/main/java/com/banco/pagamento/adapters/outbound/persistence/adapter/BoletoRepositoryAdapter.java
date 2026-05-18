package com.banco.pagamento.adapters.outbound.persistence.adapter;

import com.banco.pagamento.adapters.outbound.persistence.entity.BoletoEntity;
import com.banco.pagamento.adapters.outbound.persistence.mapper.BoletoMapper;
import com.banco.pagamento.adapters.outbound.persistence.repository.BoletoJpaRepository;
import com.banco.pagamento.application.domain.Boleto;
import com.banco.pagamento.ports.outbound.BoletoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BoletoRepositoryAdapter implements BoletoRepositoryPort {

    private final BoletoJpaRepository boletoJpaRepository;
    private final BoletoMapper boletoMapper;

    @Override
    public Optional<Boleto> buscarPorCodigo(String codigoBarra) {
        return boletoJpaRepository.findByCodigoBarra(codigoBarra)
            .map(boletoMapper::toDomain);
    }

    @Override
    public Boleto salvar(Boleto boleto) {
        BoletoEntity entity = boletoMapper.toEntity(boleto);
        BoletoEntity saved = boletoJpaRepository.save(entity);
        return boletoMapper.toDomain(saved);
    }
}
