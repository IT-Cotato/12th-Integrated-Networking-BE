package com.cotato.networking.weather.global.response;

import com.cotato.networking.weather.global.exception.ErrorReasonDTO;

public interface BaseErrorCode {
	ErrorReasonDTO getReason();

	ErrorReasonDTO getReasonHttpStatus();
}
