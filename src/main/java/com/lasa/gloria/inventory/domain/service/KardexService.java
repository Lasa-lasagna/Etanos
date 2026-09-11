package com.lasa.gloria.inventory.domain.service;

import com.lasa.gloria.common.exception.BusinessException;
import com.lasa.gloria.inventory.domain.model.Kardex;
import com.lasa.gloria.inventory.domain.repository.KardexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KardexService {

    private final KardexRepository kardexRepository;

    @Transactional
    public void registrar(Kardex kardex) {
        if (kardexRepository.existsByReferenciaTipoAndReferenciaId(
                kardex.getReferenciaTipo(), kardex.getReferenciaId())) {
            throw new BusinessException(
                    "Kardex ya registrado para " + kardex.getReferenciaTipo() + "#" + kardex.getReferenciaId(),
                    "KARDEX_DUPLICADO",
                    HttpStatus.CONFLICT);
        }
        kardexRepository.save(kardex);
    }
}
