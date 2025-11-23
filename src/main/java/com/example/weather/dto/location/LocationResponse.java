package com.example.weather.dto.location;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class LocationResponse {

    private Long id;
    private Long userId;
    private String name;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
}
