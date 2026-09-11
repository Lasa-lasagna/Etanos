package com.lasa.gloria.sales.application.mapper;

import com.lasa.gloria.sales.application.dto.response.VentaResponse;
import com.lasa.gloria.sales.application.dto.response.VentaDetalleResponse;
import com.lasa.gloria.inventory.domain.model.Salida;
import com.lasa.gloria.inventory.domain.model.SalidaDetalle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VentaMapper {

    VentaMapper INSTANCE = Mappers.getMapper(VentaMapper.class);

    @Mapping(target = "clienteNombre", source = "clienteNombre")
    @Mapping(target = "detalles", source = "salida.detalles", qualifiedByName = "detallesToResponse")
    VentaResponse toResponse(Salida salida, String clienteNombre);

    @Named("detallesToResponse")
    default List<VentaDetalleResponse> detallesToResponse(List<?> detalles) {
        return detalles.stream()
                .filter(d -> d instanceof com.lasa.gloria.inventory.domain.model.SalidaDetalle)
                .map(d -> (com.lasa.gloria.inventory.domain.model.SalidaDetalle) d)
                .map(this::toDetalleResponse)
                .toList();
    }

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    VentaDetalleResponse toDetalleResponse(SalidaDetalle detalle);
}