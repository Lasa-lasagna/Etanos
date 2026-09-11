package com.lasa.gloria.inventory.domain.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lasa.gloria.inventory.domain.model.EstadoDocumento;

@Service
public class EstadoDocumentoService {

    public List<String> getEstadoDocumento(){
        return Arrays.stream(EstadoDocumento.values())
                        .map(EstadoDocumento::name)
                        .toList();
    }
}
