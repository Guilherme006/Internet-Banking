package com.banco.pagamento.adapters.outbound.persistence.mapper;

import com.banco.pagamento.adapters.outbound.persistence.entity.ContaEntity;
import com.banco.pagamento.application.domain.Conta;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContaMapper {

    Conta toDomain(ContaEntity entity);

    ContaEntity toEntity(Conta conta);
}
