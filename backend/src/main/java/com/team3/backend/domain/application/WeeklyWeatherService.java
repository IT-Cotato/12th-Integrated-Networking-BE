package com.team3.backend.domain.application;

import com.team3.backend.domain.dao.LocationRepository;
import com.team3.backend.domain.dto.response.WeeklyWeatherResponse;
import com.team3.backend.domain.entity.Location;
import com.team3.backend.domain.infra.HourlyWeatherClient;
import com.team3.backend.domain.infra.dto.HourlyWeatherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WeeklyWeatherService {

    private final HourlyWeatherClient hourlyWeatherClient;
    private final LocationRepository locationRepository;

    public List<WeeklyWeatherResponse> getWeeklyWeather(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found: " + locationId));

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        HourlyWeatherDto dto = hourlyWeatherClient.getHourlyWeather(lat, lon);

        Map<LocalDate, List<HourlyWeatherDto.WeatherItem>> grouped = new HashMap<>();

        for (HourlyWeatherDto.WeatherItem item : dto.getList()) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(item.getDt()),
                    ZoneId.of("Asia/Seoul")
            );

            LocalDate date = dateTime.toLocalDate();

            if (!grouped.containsKey(date)) {
                grouped.put(date, new ArrayList<>());
            }
            grouped.get(date).add(item);
        }

        List<WeeklyWeatherResponse> result = new ArrayList<>();

        for (LocalDate date : grouped.keySet()) {

            List<HourlyWeatherDto.WeatherItem> items = grouped.get(date);

            List<HourlyWeatherDto.WeatherItem> amList = new ArrayList<>();
            List<HourlyWeatherDto.WeatherItem> pmList = new ArrayList<>();

            for (HourlyWeatherDto.WeatherItem it : items) {
                LocalDateTime dt = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(it.getDt()),
                        ZoneId.of("Asia/Seoul")
                );

                int hour = dt.getHour();
                if (hour < 12) {
                    amList.add(it);
                } else {
                    pmList.add(it);
                }
            }

            String amCondition = amList.isEmpty() ? "정보없음" : amList.get(0).getWeather().get(0).getDescription();
            String pmCondition = pmList.isEmpty() ? "정보없음" : pmList.get(0).getWeather().get(0).getDescription();

            double amMin = getMinTemp(amList);
            double amMax = getMaxTemp(amList);
            int amHum = getAvgHumidity(amList);

            double pmMin = getMinTemp(pmList);
            double pmMax = getMaxTemp(pmList);
            int pmHum = getAvgHumidity(pmList);

            WeeklyWeatherResponse item = WeeklyWeatherResponse.builder()
                    .day(convertToKoreanDay(date.getDayOfWeek()))
                    .amCondition(amCondition)
                    .amMinTemp(amMin)
                    .amMaxTemp(amMax)
                    .amHumidity(amHum)
                    .pmCondition(pmCondition)
                    .pmMinTemp(pmMin)
                    .pmMaxTemp(pmMax)
                    .pmHumidity(pmHum)
                    .build();

            result.add(item);
        }

        result.sort(Comparator.comparing(o -> convertDayToOrder(o.getDay())));

        return result;
    }

    private double getMinTemp(List<HourlyWeatherDto.WeatherItem> list) {
        if (list.isEmpty()) return 0.0;
        double min = Double.MAX_VALUE;
        for (HourlyWeatherDto.WeatherItem it : list) {
            double c = it.getMain().getTemp() - 273.15;
            if (c < min) min = c;
        }
        return Math.round(min * 10) / 10.0;
    }

    private double getMaxTemp(List<HourlyWeatherDto.WeatherItem> list) {
        if (list.isEmpty()) return 0.0;
        double max = -999;
        for (HourlyWeatherDto.WeatherItem it : list) {
            double c = it.getMain().getTemp() - 273.15;
            if (c > max) max = c;
        }
        return Math.round(max * 10) / 10.0;
    }

    private int getAvgHumidity(List<HourlyWeatherDto.WeatherItem> list) {
        if (list.isEmpty()) return 0;
        int sum = 0;
        for (HourlyWeatherDto.WeatherItem it : list) {
            sum += it.getMain().getHumidity();
        }
        return sum / list.size();
    }

    private String convertToKoreanDay(DayOfWeek day) {
        switch (day) {
            case MONDAY: return "월요일";
            case TUESDAY: return "화요일";
            case WEDNESDAY: return "수요일";
            case THURSDAY: return "목요일";
            case FRIDAY: return "금요일";
            case SATURDAY: return "토요일";
            case SUNDAY: return "일요일";
        }
        return "정보없음";
    }

    private int convertDayToOrder(String day) {
        switch (day) {
            case "월요일": return 1;
            case "화요일": return 2;
            case "수요일": return 3;
            case "목요일": return 4;
            case "금요일": return 5;
            case "토요일": return 6;
            case "일요일": return 7;
        }
        return 999;
    }
}
