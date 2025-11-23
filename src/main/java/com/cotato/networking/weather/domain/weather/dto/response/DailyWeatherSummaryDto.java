package com.cotato.networking.weather.domain.weather.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyWeatherSummaryDto {
    private String date;
    private HalfDayWeatherDto am;
    private HalfDayWeatherDto pm;
}

