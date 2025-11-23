package com.cotato.networking.weather.domain.weather.converter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.cotato.networking.weather.domain.weather.dto.response.WeatherOneCallResDto;
import com.cotato.networking.weather.domain.weather.dto.response.WeatherResDto;

@Component
public class WeatherConverter {

	public static WeatherResDto toDto(WeatherOneCallResDto.Current current) {
		return new WeatherResDto(
			current.getTemp(),
			current.getFeels_like(),
			current.getHumidity(),
			current.getWind_deg(),
			current.getWind_speed(),
			current.getUvi(),
			convertUnixToKoreanTime(current.getSunrise()),
			convertUnixToKoreanTime(current.getSunset()),
			current.getWeather().get(0).getMain(),
			current.getWeather().get(0).getDescription()
		);
	}

	private static String convertUnixToKoreanTime(long timestamp) {
		return Instant.ofEpochSecond(timestamp)
			.atZone(ZoneId.of("Asia/Seoul"))
			.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	}
}

