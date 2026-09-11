package com.lasa.gloria.inventory.interfaces.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lasa.gloria.inventory.domain.service.EstadoDocumentoService;
import com.lasa.gloria.inventory.domain.service.TipoInventaryService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class OptionsInventaryContoller {

    private final TipoInventaryService tipoInventaryService;
    private final EstadoDocumentoService documentoService;


    //kardex
    @GetMapping("/entradaOptions")
    public List<String> getOptionsMovimiento() {
        return tipoInventaryService.getMovimientoOptions();
    }
    
    //Ajuste
    @GetMapping("/movimientosOptions")
    public List<String> getOptionsAjuste() {
        return tipoInventaryService.getAjusteOptions();
    }

    @GetMapping("/estado")
    public List<String> getOptionEstado() {
        return documentoService.getEstadoDocumento();
    }
    

}
