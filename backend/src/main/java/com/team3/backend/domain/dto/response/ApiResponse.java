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
}
