package com.cotato.networking.weather.domain.place.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceSaveRequestDto {
    private String placeName;
    private String addressName;
    private String roadAddressName;
    private String longitude;
    private String latitude;
}
