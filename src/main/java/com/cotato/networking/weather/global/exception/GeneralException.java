package com.cotato.networking.weather.global.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public ErrorReasonDTO getErrorReason() {
        return this.errorStatus.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.errorStatus.getReasonHttpStatus();
    }
}