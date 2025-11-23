package com.team3.backend.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyWeatherResponse {

    private String day;

    private String amCondition;
    private double amMinTemp;
    private double amMaxTemp;
    private int amHumidity;

    private String pmCondition;
    private double pmMinTemp;
    private double pmMaxTemp;
    private int pmHumidity;
}

