package com.banco.pagamento.adapters.outbound.persistence.mapper;

import com.banco.pagamento.adapters.outbound.persistence.entity.BoletoEntity;
import com.banco.pagamento.application.domain.Boleto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BoletoMapper {

    Boleto toDomain(BoletoEntity entity);

    BoletoEntity toEntity(Boleto boleto);
}
