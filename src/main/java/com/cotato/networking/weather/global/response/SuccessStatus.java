package com.cotato.networking.weather.global.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum SuccessStatus implements BaseCode {

	_OK(HttpStatus.OK, "COMMON200", "성공입니다");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ReasonDTO getReason() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.build();
	}

	@Override
	public ReasonDTO getReasonHttpStauts() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.httpStatus(httpStatus)
			.build();
	}
}
