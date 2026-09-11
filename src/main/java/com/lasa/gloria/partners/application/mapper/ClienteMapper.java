package com.lasa.gloria.partners.application.mapper;

import com.lasa.gloria.partners.application.dto.response.ClienteResponse;
import com.lasa.gloria.partners.domain.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    @Mapping(target = "tipoDoc", source = "tipoDoc")
    ClienteResponse toResponse(Cliente cliente);
}