package com.team3.backend.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PinRequest {

    @NotNull(message = "pinned 값은 필수입니다.")
    private Boolean pinned;
}
