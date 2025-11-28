package com.team6.backend.domain.weather.service.command;

import com.team6.backend.domain.weather.dto.res.WeatherResponse;

public interface WeatherService {

    //위도 경도를 이용하여 지역이름과 날씨 반환
    public WeatherResponse.CurrentWeatherDTO getCurrentWeather(double lat, double lon);

    //위도 경도를 이용하여 지역이름과 시간별 날씨 반환
    WeatherResponse.HourWeatherDTO getHourlyWeather(double lat, double lon);

    //위도 경도를 이용하여 지역이름과 날짜별 날씨 반환
    WeatherResponse.WeeklyForecastDTO getWeeklyWeather(double lat, double lon);
}
