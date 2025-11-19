package com.cotato.networking.weather.domain.auth.converter;

import com.cotato.networking.weather.domain.auth.dto.request.AuthLoginRequestDto;
import com.cotato.networking.weather.domain.auth.dto.request.AuthSignUpRequestDto;
import com.cotato.networking.weather.domain.auth.dto.response.AuthResponseDto;
import com.cotato.networking.weather.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthConverter {

    // 회원가입 DTO -> Entity
    public User toEntity(AuthSignUpRequestDto dto, String encodedPassword) {
        return User.builder()
                .username(dto.getUsername())
                .password(encodedPassword)
                .build();
    }

    // Entity -> DTO (응답용)
    public AuthResponseDto toDto(User user) {
        return AuthResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
