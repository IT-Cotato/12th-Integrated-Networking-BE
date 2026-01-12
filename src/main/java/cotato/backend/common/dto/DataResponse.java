package cotato.backend.common.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;


import lombok.Getter;

@Getter
public class DataResponse<T> extends BaseResponse {

	private final T data;

	private DataResponse(String code, String message, T data) {
		super(code, message);
		this.data = data;
	}

	public static <T> DataResponse<T> of(T data) {
		return new DataResponse<>("SUCCESS", "성공", data);
	}
}