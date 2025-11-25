package com.team3.backend.global.exception;

public class LocationNotFoundException extends RuntimeException {

    public LocationNotFoundException() {
        super("LOCATION_NOT_FOUND");
    }
}

