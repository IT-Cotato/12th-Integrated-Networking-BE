package com.cotato.networking.weather.domain.weather.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HalfDayWeatherDto {
    private String weather;
    private double avgTemp;
    private double pop;
}

