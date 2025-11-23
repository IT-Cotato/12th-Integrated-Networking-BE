package com.cotato.networking.weather.domain.weather.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cotato.networking.weather.domain.weather.dto.response.AirPollutionResDto;
import com.cotato.networking.weather.domain.weather.dto.response.DailyWeatherSummaryDto;
import com.cotato.networking.weather.domain.weather.dto.response.HalfDayWeatherDto;
import com.cotato.networking.weather.domain.weather.dto.response.WeatherCurrentResDto;
import com.cotato.networking.weather.domain.weather.dto.response.WeatherForecastResDto;
import com.cotato.networking.weather.domain.weather.dto.response.WeatherOneCallResDto;
import com.cotato.networking.weather.global.exception.ErrorStatus;
import com.cotato.networking.weather.global.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.api-key}")
    private String apiKey;

    @Value("${weather.forecast-url}")
    private String forecastUrl;

    @Value("${weather.current-url}")
    private String currentUrl;

    @Value("${weather.air-url}")
    private String airUrl;

    @Value("${weather.oneCall-Url}")
    private String oneCallUrl;

    private String buildUrl(String baseUrl, double lat, double lon) {
        return baseUrl + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric&lang=kr";
    }

    public List<DailyWeatherSummaryDto> getFiveDayForecast(double lat, double lon) {

        // Forecast API 호출
        String forecastRequestUrl = buildUrl(this.forecastUrl, lat, lon);
        WeatherForecastResDto forecastResponse = restTemplate.getForObject(forecastRequestUrl, WeatherForecastResDto.class);

        if (forecastResponse == null) {
            throw new GeneralException(ErrorStatus.EXTERNAL_API_NO_RESPONSE);
        }

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        Map<LocalDate, List<WeatherForecastResDto.WeatherItem>> grouped = forecastResponse.getList().stream()
                .collect(Collectors.groupingBy(WeatherForecastResDto.WeatherItem::getKstDate));

        List<LocalDate> targetDates = grouped.keySet().stream()
                .filter(date -> !date.isBefore(today))
                .sorted()
                .limit(5)
                .toList();

        List<DailyWeatherSummaryDto> summaries = new ArrayList<>();

        for (LocalDate date : targetDates) {
            List<WeatherForecastResDto.WeatherItem> items = grouped.get(date);

            List<WeatherForecastResDto.WeatherItem> am = items.stream()
                    .filter(i -> i.getHour() < 12) // 오전
                    .toList();

            List<WeatherForecastResDto.WeatherItem> pm = items.stream()
                    .filter(i -> i.getHour() >= 12) // 오후
                    .toList();

            HalfDayWeatherDto amWeather;

            // 오전 데이터가 없고 오늘이면 current API 호출, 강수확률은 pm에서
            if (date.equals(today) && am.isEmpty()) {
                String currentRequestUrl = buildUrl(currentUrl, lat, lon);
                WeatherCurrentResDto currentResponse = restTemplate.getForObject(currentRequestUrl, WeatherCurrentResDto.class);

                if (currentResponse == null) {
                    throw new GeneralException(ErrorStatus.EXTERNAL_API_NO_RESPONSE);
                }

                double pop = summarizeHalfDay(pm).getPop();

                amWeather = new HalfDayWeatherDto(
                        currentResponse.getWeather().get(0).getMain(),
                        currentResponse.getMain().getTemp(),
                        pop
                );
            } else {
                amWeather = summarizeHalfDay(am);
            }

            summaries.add(new DailyWeatherSummaryDto(
                    date.toString(),
                    amWeather,
                    summarizeHalfDay(pm)
            ));
        }

        return summaries;
    }

    private HalfDayWeatherDto summarizeHalfDay(List<WeatherForecastResDto.WeatherItem> items) {
        if (items.isEmpty()) return new HalfDayWeatherDto("정보 없음", 0, 0);

        return new HalfDayWeatherDto(
                mostFrequentWeather(items),
                averageTemp(items),
                averagePop(items)
        );
    }


    private String mostFrequentWeather(List<WeatherForecastResDto.WeatherItem> items) {
        return items.stream()
                .map(i -> i.getWeather().get(0).getMain())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue()) // 최빈값
                .map(Map.Entry::getKey)
                .orElse("정보 없음");
    }

    private double averageTemp(List<WeatherForecastResDto.WeatherItem> items) {
        return items.stream()
                .mapToDouble(i -> i.getMain().getTemp())
                .average()
                .orElse(0);
    }

    private double averagePop(List<WeatherForecastResDto.WeatherItem> items) {
        return items.stream()
                .mapToDouble(i -> i.getPop() * 100) // % 변환
                .average()
                .orElse(0);
    }

    public AirPollutionResDto getAirPollution(double lat, double lon) {
        String url = airUrl +
                "?lat=" + lat +
                "&lon=" + lon +
                "&appid=" + apiKey;

        AirPollutionResDto response = restTemplate.getForObject(url, AirPollutionResDto.class);

        if (response == null) {
            throw new GeneralException(ErrorStatus.EXTERNAL_API_NO_RESPONSE);
        }

        return response;
    }

    public WeatherOneCallResDto getCurrentWeather(double lat, double lon) {
        String url = oneCallUrl +
                "?lat=" + lat +
                "&lon=" + lon +
                "&exclude=minutely,alerts" +
                "&appid=" + apiKey +
                "&units=metric&lang=kr";

        WeatherOneCallResDto response = restTemplate.getForObject(url, WeatherOneCallResDto.class);

        if (response == null) {
            throw new GeneralException(ErrorStatus.EXTERNAL_API_NO_RESPONSE);
        }

        return response;
    }

}
