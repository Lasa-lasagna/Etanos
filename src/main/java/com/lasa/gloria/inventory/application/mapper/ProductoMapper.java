package com.lasa.gloria.inventory.application.mapper;

import com.lasa.gloria.inventory.application.dto.response.ProductoResponse;
import com.lasa.gloria.inventory.domain.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductoMapper {

    @Mapping(target = "marcaId", source = "marca.id")
    @Mapping(target = "marcaNombre", source = "marca.nombre")
    @Mapping(target = "stockActual", source = "inventario.stockActual")
    ProductoResponse toResponse(Producto producto);
}
