package com.team3.backend.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "새로운 장소 생성 요청 DTO")
public class CreateLocationRequest {

    @NotBlank(message = "장소 이름은 필수입니다.")
    @Schema(description = "장소 이름", example = "코엑스")
    private String placeName;

    @NotNull(message = "위도는 필수입니다.")
    @Schema(description = "위도 (Latitude)", example = "37.51187")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    @Schema(description = "경도 (Longitude)", example = "127.05923")
    private Double longitude;
}
