package com.lasa.gloria.partners.application.mapper;

import com.lasa.gloria.partners.application.dto.response.ProveedorResponse;
import com.lasa.gloria.partners.domain.model.Proveedor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProveedorMapper {

    ProveedorMapper INSTANCE = Mappers.getMapper(ProveedorMapper.class);

    ProveedorResponse toResponse(Proveedor proveedor);
}