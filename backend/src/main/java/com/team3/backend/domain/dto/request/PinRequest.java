package com.team3.backend.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "장소 고정/해제 요청 DTO")
public class PinRequest {

    @NotNull(message = "pinned 값은 필수입니다.")
    @Schema(description = "장소 고정 여부 (true: 고정, false: 해제)", example = "true")
    private Boolean pinned;
}
