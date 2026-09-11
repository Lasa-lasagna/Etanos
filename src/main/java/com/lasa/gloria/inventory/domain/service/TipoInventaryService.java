package com.lasa.gloria.inventory.domain.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.lasa.gloria.inventory.domain.model.TipoAjuste;
import com.lasa.gloria.inventory.domain.model.TipoMovimientoKardex;

@Service
public class TipoInventaryService {
    //Capricho mio algo q Galenos hacia en sus sistema

    public List<String> getAjusteOptions(){
        return Arrays.stream(TipoAjuste.values())
                .map(TipoAjuste::name)
                .collect(Collectors.toList());
    }

    public List<String> getMovimientoOptions(){
        return Arrays.stream(TipoMovimientoKardex.values())
                    .map(TipoMovimientoKardex::name)
                    .collect(Collectors.toList());
    }
}
