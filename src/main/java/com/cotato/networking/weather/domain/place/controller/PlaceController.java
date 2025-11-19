package com.cotato.networking.weather.domain.place.controller;

import com.cotato.networking.weather.domain.place.dto.request.PlaceSaveRequestDto;
import com.cotato.networking.weather.domain.place.dto.response.PlaceResponseDto;
import com.cotato.networking.weather.domain.place.service.PlaceService;
import com.cotato.networking.weather.global.config.AuthInterceptor;
import com.cotato.networking.weather.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    // 장소 등록
    @PostMapping
    public ApiResponse<PlaceResponseDto> createPlace(
            @SessionAttribute(AuthInterceptor.SESSION_USER_ID) Long userId,
            @RequestBody PlaceSaveRequestDto requestDto) {

        PlaceResponseDto response = placeService.create(userId, requestDto);
        return ApiResponse.onSuccess(response);
    }

    // 장소 목록 조회
    @GetMapping
    public ApiResponse<List<PlaceResponseDto>> getPlaces(
            @SessionAttribute(AuthInterceptor.SESSION_USER_ID) Long userId) {

        return ApiResponse.onSuccess(placeService.findAll(userId));
    }

    // 장소 삭제
    @DeleteMapping("/{placeId}")
    public ApiResponse<String> deletePlace(
            @SessionAttribute(AuthInterceptor.SESSION_USER_ID) Long userId,
            @PathVariable Long placeId) {

        placeService.deletePlace(placeId, userId);
        return ApiResponse.onSuccess("장소가 성공적으로 삭제되었습니다.");
    }
}
