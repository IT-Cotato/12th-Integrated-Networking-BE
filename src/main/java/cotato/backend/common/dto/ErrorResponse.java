package cotato.backend.common.dto;

import org.springframework.http.HttpStatus;

import cotato.backend.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
@Getter
public class ErrorResponse {

	private final String code;
	private final String message;
	private final int status;

	private ErrorResponse(String code, String message, int status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}

	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(
				errorCode.getCode(),
				errorCode.getMessage(),
				errorCode.getHttpStatus().value()
		);
	}
}