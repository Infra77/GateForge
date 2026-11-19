package com.infra.gateforge.management.exception;

public class InactiveOrganisationException extends RuntimeException{
    public InactiveOrganisationException(String message){
        super(message);
    }
}
