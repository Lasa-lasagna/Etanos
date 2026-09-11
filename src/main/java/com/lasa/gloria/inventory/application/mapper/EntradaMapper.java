package com.lasa.gloria.inventory.application.mapper;

import com.lasa.gloria.inventory.application.dto.response.EntradaDetalleResponse;
import com.lasa.gloria.inventory.application.dto.response.EntradaResponse;
import com.lasa.gloria.inventory.domain.model.Entrada;
import com.lasa.gloria.inventory.domain.model.EntradaDetalle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntradaMapper {

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    EntradaDetalleResponse toDetalleResponse(EntradaDetalle detalle);

    EntradaResponse toResponse(Entrada entrada);
}
