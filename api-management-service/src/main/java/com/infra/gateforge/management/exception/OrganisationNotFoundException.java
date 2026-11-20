package com.infra.gateforge.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrganisationNotFoundException extends RuntimeException {
    public OrganisationNotFoundException(String message) {
        super(message);
    }
}
