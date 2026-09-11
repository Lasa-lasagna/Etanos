package com.lasa.gloria.inventory.application.mapper;

import com.lasa.gloria.inventory.application.dto.response.AjusteDetalleResponse;
import com.lasa.gloria.inventory.application.dto.response.AjusteResponse;
import com.lasa.gloria.inventory.domain.model.Ajuste;
import com.lasa.gloria.inventory.domain.model.AjusteDetalle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AjusteMapper {

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    AjusteDetalleResponse toDetalleResponse(AjusteDetalle detalle);

    AjusteResponse toResponse(Ajuste ajuste);
}
