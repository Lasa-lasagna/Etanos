package com.lasa.gloria.inventory.application.mapper;

import com.lasa.gloria.inventory.application.dto.response.MarcaResponse;
import com.lasa.gloria.inventory.domain.model.Marca;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MarcaMapper {
    MarcaResponse toResponse(Marca marca);
}
