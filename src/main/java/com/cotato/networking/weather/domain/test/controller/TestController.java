package com.cotato.networking.weather.domain.test.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cotato.networking.weather.domain.test.dto.request.TestReqDto;
import com.cotato.networking.weather.domain.test.dto.response.TestResDto;
import com.cotato.networking.weather.domain.test.service.TestService;
import com.cotato.networking.weather.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping
    @Operation(summary = "예시 API", description = "test를 생성합니다.")
    public ApiResponse<TestResDto> create(@RequestBody @Valid TestReqDto request) {
        TestResDto response = testService.createPost(request);
        return ApiResponse.onSuccess(response);
    }
}
