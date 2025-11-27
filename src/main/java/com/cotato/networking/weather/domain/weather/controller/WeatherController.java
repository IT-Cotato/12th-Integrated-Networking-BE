package com.cotato.networking.weather.domain.weather.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cotato.networking.weather.domain.weather.dto.response.AirPollutionResDto;
import com.cotato.networking.weather.domain.weather.dto.response.DailyWeatherSummaryDto;
import com.cotato.networking.weather.domain.weather.dto.response.WeatherOneCallResDto;
import com.cotato.networking.weather.domain.weather.service.WeatherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/5days")
    @CrossOrigin(origins = "*")
    @Operation(
            summary = "5일간 날씨 예보 조회 API",
            description = """
                3시간 단위 OpenWeather 예보 데이터를 기반으로 하루를 오전(am)과 오후(pm)으로 나누어 5일간 요약 정보를 제공합니다.

                - weather: 해당 시간대에서 가장 많이 나타난 날씨 상태
                - avgTemp: 평균 기온 (°C)
                - pop: 강수 확률, 백분율(%)로 환산된 값 (예: 20.0은 20% 확률)
            """
    )
    public List<DailyWeatherSummaryDto> getForecast(
            @Parameter(description = "위도", required = true)
            @RequestParam double lat,

            @Parameter(description = "경도", required = true)
            @RequestParam double lon
    ) {
        return weatherService.getFiveDayForecast(lat, lon);
    }

    @GetMapping("/air")
    @CrossOrigin(origins = "*")
    @Operation(summary = "대기 오염 정보 조회", description = "OpenWeather의 대기 오염 API를 통해 미세먼지 등 대기 질 정보를 조회합니다.")
    public AirPollutionResDto getAirPollution(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        return weatherService.getAirPollution(lat, lon);
    }

    @GetMapping("/onecall")
    @CrossOrigin(origins = "*")
    @Operation(summary = "현재 날씨 조회", description = "OpenWeather의 onecall API를 통해 현재 날씨, 시간별 예보 등을 제공합니다.")
    public WeatherOneCallResDto getCurrentWeather(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        return weatherService.getCurrentWeather(lat, lon);
    }
}
