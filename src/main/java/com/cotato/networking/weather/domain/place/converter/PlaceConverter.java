package com.cotato.networking.weather.domain.place.converter;

import com.cotato.networking.weather.domain.place.dto.request.PlaceSaveRequestDto;
import com.cotato.networking.weather.domain.place.dto.response.PlaceResponseDto;
import com.cotato.networking.weather.domain.place.entity.Place;
import com.cotato.networking.weather.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PlaceConverter {

    // DTO -> Entity
    public Place toEntity(PlaceSaveRequestDto dto, User user) {
        return Place.builder()
                .placeName(dto.getPlaceName())
                .addressName(dto.getAddressName())
                .roadAddressName(dto.getRoadAddressName())
                .longitude(dto.getLongitude())
                .latitude(dto.getLatitude())
                .isPinned(false)
                .user(user)
                .build();
    }

    // Entity -> DTO (응답용)
    public PlaceResponseDto toDto(Place place) {
        return PlaceResponseDto.builder()
                .id(place.getId())
                .placeName(place.getPlaceName())
                .addressName(place.getAddressName())
                .roadAddressName(place.getRoadAddressName())
                .longitude(place.getLongitude())
                .latitude(place.getLatitude())
                .isPinned(place.isPinned())
                .build();
    }
}
