package com.team3.backend.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

    private String code;
    private String message;
    private boolean success;
    private T results;

    public static <T> ApiResponse<T> ok(T results) {
        return ApiResponse.<T>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(results)
                .build();
    }

    public static ApiResponse<?> error(String code, String message) {
        return ApiResponse.builder()
                .code(code)
                .message(message)
                .success(false)
                .results(null)
                .build();
    }
}
