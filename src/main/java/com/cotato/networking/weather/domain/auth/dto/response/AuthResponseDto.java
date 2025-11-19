package com.cotato.networking.weather.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {
    private Long id;
    private String username;
}
