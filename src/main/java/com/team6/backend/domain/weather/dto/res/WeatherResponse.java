package com.team6.backend.domain.weather.dto.res;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class WeatherResponse {

    @Getter
    @Builder
    public static class CurrentWeatherDTO {
        private String city;
        private double temperature;
        private String description;
        private double feelTemperature; //체감온도
        private int humidity; // 습도
        private double windSpeed; // 풍속
        private String windDirection; // 풍향 (각도)
        private String sunriseTime; // 일출 시간

        private String pm10; // 미세먼지
        private String pm25; // 초미세먼지

        private String uv; // 자외선
    }

    @Getter
    @Builder
    public static class HourWeatherDTO{
        private String city;
        private List<HourList> weatherList;
    }

    @Getter
    @Builder
    public static class HourList{
        private String time;
        private double temperature;
        private String description;
    }


    @Getter
    @Builder
    public static class WeeklyForecastDTO {
        private String city;
        private List<DailyWeather> dailyList;
    }

    @Getter
    @Builder
    public static class DailyWeather {
        private String date;
        private String dayOfWeek;

        private double minTemp;
        private double maxTemp;

        private AmPm am;
        private AmPm pm;
    }

    @Getter
    @Builder
    public static class AmPm {
        private String description;
        private int rain;
    }
}
