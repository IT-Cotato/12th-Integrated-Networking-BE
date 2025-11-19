package com.team3.backend.api;

import com.team3.backend.domain.application.WeatherService;
import com.team3.backend.domain.dto.response.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    public WeatherResponse getWeather(@RequestParam double lat,
                                      @RequestParam double lon) {

        return weatherService.getWeather(lat,lon);
    }

}
