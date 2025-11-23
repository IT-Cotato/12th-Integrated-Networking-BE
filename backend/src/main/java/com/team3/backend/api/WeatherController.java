package com.team3.backend.api;

import com.team3.backend.domain.application.HourlyWeatherService;
import com.team3.backend.domain.application.WeatherService;
import com.team3.backend.domain.application.WeeklyWeatherService;
import com.team3.backend.domain.dto.response.ApiResponse;
import com.team3.backend.domain.dto.response.HourlyWeatherResponse;
import com.team3.backend.domain.dto.response.WeatherResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.team3.backend.domain.dto.response.WeeklyWeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
@Tag(name = "Weather API", description = "현재 날씨 및 대기질 정보 조회 기능 제공")
public class WeatherController {

    private final WeatherService weatherService;
    private final HourlyWeatherService hourlyWeatherService;
    private final WeeklyWeatherService weeklyWeatherService;

    @Operation(summary = "현재 날씨 및 대기질 조회", description = "특정 locationId에 저장된 장소의 현재 날씨와 대기질 정보를 조회합니다.")
    @GetMapping("/current")
    public WeatherResponse getWeather(
            @Parameter(description = "조회할 장소의 ID", example = "1")
            @RequestParam Long locationId) {
    public ApiResponse<WeatherResponse> getWeather(@RequestParam Long locationId) {

        WeatherResponse response = weatherService.getWeather(locationId);

        return ApiResponse.<WeatherResponse>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(response)
                .build();
    }

    @GetMapping("/hourly")
    public ApiResponse<List<HourlyWeatherResponse>> getHourlyWeather(@RequestParam Long locationId) {

        List<HourlyWeatherResponse> list = hourlyWeatherService.getHourlyWeather(locationId);

        return ApiResponse.<List<HourlyWeatherResponse>>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(list)
                .build();
    }

    @GetMapping("/weekly")
    public ApiResponse<List<WeeklyWeatherResponse>> getWeeklyWeather(@RequestParam Long locationId) {

        List<WeeklyWeatherResponse> list = weeklyWeatherService.getWeeklyWeather(locationId);

        return ApiResponse.<List<WeeklyWeatherResponse>>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(list)
                .build();
    }


}
