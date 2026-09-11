package com.lasa.gloria.partners.application.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.lasa.gloria.partners.domain.model.TipoDocumento;

@Service
public class OptionsService {
    public List<String> getOptionsDocuments() {
        return Arrays.stream(TipoDocumento.values())
                .map(TipoDocumento::name)
                .collect(Collectors.toList());
    }
}
