package com.cotato.networking.weather.domain.weather.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherCurrentResDto {
    private WeatherForecastResDto.Main main;
    private List<WeatherForecastResDto.Weather> weather;
}

