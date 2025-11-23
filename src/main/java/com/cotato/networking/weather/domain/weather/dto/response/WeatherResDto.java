package com.cotato.networking.weather.domain.weather.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResDto {
	private double temp;
	private double feelsLike;
	private int humidity;
	private int windDirection;
	private double windSpeed;
	private double uvi;

	private String sunrise;
	private String sunset;

	private String main;
	private String description;
}

