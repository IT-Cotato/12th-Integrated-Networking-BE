package com.team3.backend.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "장소 ID 응답 DTO")
public class LocationIdResponse {

    @Schema(description = "생성/수정된 장소의 고유 ID", example = "1")
    private Long locationId;
}
