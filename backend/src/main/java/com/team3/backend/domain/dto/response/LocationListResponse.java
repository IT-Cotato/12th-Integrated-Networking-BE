package com.team3.backend.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "장소 목록 조회 항목 DTO")
public class LocationListResponse {

    @Schema(description = "장소 ID", example = "1")
    private Long locationId;
    @Schema(description = "장소 이름", example = "코엑스")
    private String placeName;
    @Schema(description = "위도", example = "37.51187")
    private Double latitude;
    @Schema(description = "경도", example = "127.05923")
    private Double longitude;
    @Schema(description = "고정된 장소 여부", example = "true")
    private Boolean pinned;
}
