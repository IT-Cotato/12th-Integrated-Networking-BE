package com.cotato.networking.weather.domain.test.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "테스트 응답 DTO")
public record TestResDto(

	Long id,

	String title,

	String content,

	LocalDateTime createdAt
) {
}
