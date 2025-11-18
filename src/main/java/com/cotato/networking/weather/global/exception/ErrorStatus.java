package com.cotato.networking.weather.global.exception;



import org.springframework.http.HttpStatus;

import com.cotato.networking.weather.global.response.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

	// Place Error
	PLACE_ALREADY_EXISTS(HttpStatus.CONFLICT, "P-001", "이미 등록된 장소입니다."),

	// Common
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

	// User & Auth
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "해당 사용자를 찾을 수 없습니다."),
	USER_PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "USER401", "비밀번호가 일치하지 않습니다."),
	USER_EXISTS(HttpStatus.CONFLICT, "USER409", "이미 존재하는 사용자입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ErrorReasonDTO getReason() {
		return ErrorReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.build();
	}


	@Override
	public ErrorReasonDTO getReasonHttpStatus() {
		return ErrorReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.httpStatus(httpStatus)
			.build();
	}
}
