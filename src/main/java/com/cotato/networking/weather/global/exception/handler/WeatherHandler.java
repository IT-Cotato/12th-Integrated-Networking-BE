package com.cotato.networking.weather.global.exception.handler;

import com.cotato.networking.weather.global.exception.ErrorStatus;

public class WeatherHandler extends RuntimeException {
	public WeatherHandler(ErrorStatus errorStatus) {
		super(errorStatus.getMessage());
	}
}
