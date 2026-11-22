package com.infra.gateforge.management.controller;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {PlatformOrganisationController.class, TenantOrganisationController.class})
public class OrganisationConflictHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException exception) {
        String constraintName = exception.getConstraintName();
        if (constraintName != null) {
            // MySQL can prefix the key name with the table name.
            String key = constraintName.substring(constraintName.lastIndexOf('.') + 1);
            if (key.equals("uk_organisations_name")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Organisation name already exists");
            }
            if (key.equals("uk_organisations_slug")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Organisation slug already exists");
            }
        }
        throw exception;
    }
}
