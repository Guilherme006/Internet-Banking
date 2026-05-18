package com.banco.pagamento.adapters.outbound.persistence.adapter;

import com.banco.pagamento.adapters.outbound.persistence.entity.TransacaoEntity;
import com.banco.pagamento.adapters.outbound.persistence.mapper.TransacaoMapper;
import com.banco.pagamento.adapters.outbound.persistence.repository.TransacaoJpaRepository;
import com.banco.pagamento.application.domain.Transacao;
import com.banco.pagamento.application.usecase.ConsultarExtratoQuery;
import com.banco.pagamento.application.usecase.ExtratoResultado;
import com.banco.pagamento.ports.outbound.TransacaoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransacaoRepositoryAdapter implements TransacaoRepositoryPort {

    private final TransacaoJpaRepository transacaoJpaRepository;
    private final TransacaoMapper transacaoMapper;

    @Override
    public Transacao salvar(Transacao transacao) {
        TransacaoEntity entity = transacaoMapper.toEntity(transacao);
        TransacaoEntity saved = transacaoJpaRepository.save(entity);
        return transacaoMapper.toDomain(saved);
    }

    @Override
    public ExtratoResultado buscarExtrato(ConsultarExtratoQuery query) {
        PageRequest pageable = PageRequest.of(query.pagina(), query.tamanho());
        Page<TransacaoEntity> page = transacaoJpaRepository.buscarExtrato(
            query.numeroConta(),
            query.tipo(),
            query.dataInicio(),
            query.dataFim(),
            pageable
        );

        return new ExtratoResultado(
            page.getContent().stream().map(transacaoMapper::toDomain).toList(),
            page.getTotalElements(),
            page.getNumber(),
            page.getSize(),
            page.getTotalPages()
        );
    }
}
