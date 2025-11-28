package com.team6.backend.domain.weather.controller;

import com.team6.backend.common.dto.DataResponse;
import com.team6.backend.domain.weather.dto.res.WeatherResponse;
import com.team6.backend.domain.weather.service.command.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    //현재 날씨 불러오기
    @GetMapping("/weather")
    public ResponseEntity<DataResponse<WeatherResponse.CurrentWeatherDTO>> getCurrentWeather(
            @RequestParam double lat,
            @RequestParam double lon
    ){
        WeatherResponse.CurrentWeatherDTO response = weatherService.getCurrentWeather(lat, lon);

        return ResponseEntity.ok(DataResponse.from(response));
    }

    //시간별 날씨 불러오기
    @GetMapping("/forecast/hourly")
    public ResponseEntity<DataResponse<WeatherResponse.HourWeatherDTO>> getHourlyWeather(
            @RequestParam double lat,
            @RequestParam double lon
    ){
        WeatherResponse.HourWeatherDTO response = weatherService.getHourlyWeather(lat, lon);

        return ResponseEntity.ok(DataResponse.from(response));
    }

    //일별 날씨 불러오기
    @GetMapping("/forecast/weekly")
    public ResponseEntity<DataResponse<WeatherResponse.WeeklyForecastDTO>> getWeeklyWeather(
            @RequestParam double lat,
            @RequestParam double lon
    ){
        WeatherResponse.WeeklyForecastDTO response = weatherService.getWeeklyWeather(lat, lon);

        return ResponseEntity.ok(DataResponse.from(response));
    }
}
