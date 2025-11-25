package com.team3.backend.global.exception;

import com.team3.backend.domain.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleLocationNotFound() {
        return ResponseEntity
                .status(404)
                .body(ApiResponse.error("LOCATION_NOT_FOUND", "존재하지 않는 위치입니다."));
    }

    @ExceptionHandler(ForbiddenLocationException.class)
    public ResponseEntity<ApiResponse<?>> handleForbiddenLocation() {
        return ResponseEntity
                .status(403)
                .body(ApiResponse.error("FORBIDDEN_LOCATION", "해당 위치에 접근할 수 없습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleEtc(Exception e) {
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error("INTERNAL_ERROR", "알 수 없는 오류가 발생했습니다."));
    }
}