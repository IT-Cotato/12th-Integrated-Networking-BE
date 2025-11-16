package com.cotato.networking.weather.domain.test.converter;

import com.cotato.networking.weather.domain.test.dto.request.TestReqDto;
import com.cotato.networking.weather.domain.test.dto.response.TestResDto;
import com.cotato.networking.weather.domain.test.entity.Test;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConverter {

    public Test toEntity(TestReqDto request) {
        return Test.builder()
            .title(request.title())
            .content(request.content())
            .build();
    }

    public TestResDto toResponse(Test test) {
        return TestResDto.builder()
            .id(test.getId())
            .title(test.getTitle())
            .content(test.getContent())
            .createdAt(test.getCreatedAt())
            .build();
    }
}
