package com.team3.backend.domain.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HourlyWeatherResponse {
    private String hour;
    private double temperature;
    private String condition;
}
