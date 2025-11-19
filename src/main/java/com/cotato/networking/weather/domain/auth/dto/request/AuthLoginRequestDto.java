package com.cotato.networking.weather.domain.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthLoginRequestDto {
    private String username;
    private String password;
}
