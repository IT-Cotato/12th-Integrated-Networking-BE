package com.team3.backend.api;

import com.team3.backend.domain.application.HourlyWeatherService;
import com.team3.backend.domain.application.WeatherService;
import com.team3.backend.domain.dto.response.HourlyWeatherListResponse;
import com.team3.backend.domain.dto.response.HourlyWeatherResponse;
import com.team3.backend.domain.dto.response.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final HourlyWeatherService hourlyWeatherService;

    @GetMapping("/current")
    public WeatherResponse getWeather(@RequestParam Long locationId) {

        return weatherService.getWeather(locationId);
    }

    @GetMapping("/hourly")
    public HourlyWeatherListResponse getHourlyWeather(@RequestParam Long locationId) {

        List<HourlyWeatherResponse> list = hourlyWeatherService.getHourlyWeather(locationId);

        return HourlyWeatherListResponse.builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(list)
                .build();
    }


}
