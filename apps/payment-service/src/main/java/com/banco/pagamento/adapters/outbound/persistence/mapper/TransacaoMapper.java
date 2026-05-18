package com.banco.pagamento.adapters.outbound.persistence.mapper;

import com.banco.pagamento.adapters.outbound.persistence.entity.TransacaoEntity;
import com.banco.pagamento.application.domain.Transacao;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransacaoMapper {

    Transacao toDomain(TransacaoEntity entity);

    TransacaoEntity toEntity(Transacao transacao);
}
