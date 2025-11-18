package com.cotato.networking.weather.domain.place.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponseDto {
    private Long id;
    private String placeName;
    private String addressName;
    private String roadAddressName;
    private String longitude;
    private String latitude;
    private boolean isPinned;
}
