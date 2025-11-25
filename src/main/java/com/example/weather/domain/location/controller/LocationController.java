package com.example.weather.domain.location.controller;

import com.example.weather.domain.location.dto.LocationCreateRequest;
import com.example.weather.domain.location.dto.LocationResponse;
import com.example.weather.domain.location.service.LocationService;
import com.example.weather.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Location", description = "위치 관리 API")
@RestController
@RequestMapping("/api/users/{userId}/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(
            summary = "위치 등록",
            description = "사용자의 위치를 등록합니다."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LocationResponse> createLocation(
            @PathVariable Long userId,
            @RequestBody LocationCreateRequest request
    ) {
        LocationResponse response = locationService.createLocation(userId, request);
        return ApiResponse.success(response);
    }

    @Operation(
            summary = "위치 목록 조회",
            description = "사용자의 모든 위치 목록을 조회합니다."
    )
    @GetMapping
    public ApiResponse<List<LocationResponse>> getLocations(
            @PathVariable Long userId
    ) {
        List<LocationResponse> locations = locationService.getLocations(userId);
        return ApiResponse.success(locations);
    }

    @Operation(
            summary = "위치 단건 조회",
            description = "사용자의 특정 위치를 조회합니다."
    )
    @GetMapping("/{locationId}")
    public ApiResponse<LocationResponse> getLocation(
            @PathVariable Long userId,
            @PathVariable Long locationId
    ) {
        LocationResponse location = locationService.getLocation(userId, locationId);
        return ApiResponse.success(location);
    }

    @Operation(
            summary = "위치 삭제",
            description = "사용자의 특정 위치를 삭제합니다."
    )
    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteLocation(
            @PathVariable Long userId,
            @PathVariable Long locationId
    ) {
        locationService.deleteLocation(userId, locationId);
        return ApiResponse.success();
    }
}
