package com.team3.backend.global.exception;

public class ForbiddenLocationException extends RuntimeException {

    public ForbiddenLocationException() {
        super("FORBIDDEN_LOCATION");
    }
}