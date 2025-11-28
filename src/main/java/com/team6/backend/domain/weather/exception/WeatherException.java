package com.team6.backend.domain.weather.exception;

import com.team6.backend.common.exception.AppException;
import com.team6.backend.common.exception.BaseErrorCode;
import com.team6.backend.common.exception.ErrorCode;
import com.team6.backend.domain.weather.exception.code.WeatherErrorCode;

public class WeatherException extends AppException {
    public WeatherException(BaseErrorCode code) {
        super(code);
    }
}
