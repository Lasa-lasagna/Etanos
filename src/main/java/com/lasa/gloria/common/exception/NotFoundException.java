package com.lasa.gloria.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException {

    public NotFoundException(String resource, Object id) {
        super(resource + " no encontrado: " + id, "NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
