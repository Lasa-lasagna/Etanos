package com.lasa.gloria.inventory.application.mapper;

import com.lasa.gloria.inventory.application.dto.response.SalidaDetalleResponse;
import com.lasa.gloria.inventory.application.dto.response.SalidaResponse;
import com.lasa.gloria.inventory.domain.model.Salida;
import com.lasa.gloria.inventory.domain.model.SalidaDetalle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalidaMapper {

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    SalidaDetalleResponse toDetalleResponse(SalidaDetalle detalle);

    SalidaResponse toResponse(Salida salida);
}
