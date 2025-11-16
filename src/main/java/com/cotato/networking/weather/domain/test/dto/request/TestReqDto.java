package com.cotato.networking.weather.domain.test.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "테스트 요청 Dto")
public record TestReqDto(

	@Schema(description = "타이틀")
	String title,

	String content
) {
}
