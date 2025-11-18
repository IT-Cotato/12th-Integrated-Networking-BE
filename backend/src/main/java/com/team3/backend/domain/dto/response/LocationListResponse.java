package com.team3.backend.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LocationListResponse {

    private Long locationId;
    private String placeName;
    private Double latitude;
    private Double longitude;
    private Boolean pinned;
}
