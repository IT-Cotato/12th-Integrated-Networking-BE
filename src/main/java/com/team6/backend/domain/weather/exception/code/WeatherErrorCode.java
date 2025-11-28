package com.team6.backend.domain.weather.exception.code;

import com.team6.backend.common.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WeatherErrorCode implements BaseErrorCode {

    WEATHER_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "날씨 데이터를 가져오는 중 문제가 발생했습니다.", "WE-001"),
    AIR_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "대기질 데이터를 가져오는 중 문제가 발생했습니다.", "WE-002"),
    UV_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "자외선 지수를 가져오는 중 문제가 발생했습니다.", "WE-003"),
    INVALID_LAN_LON(HttpStatus.BAD_REQUEST, "잘못된 위도, 경도 값을 입력했습니다.", "WE-004")
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
