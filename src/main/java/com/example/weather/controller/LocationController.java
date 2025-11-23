package com.example.weather.controller;

import com.example.weather.dto.location.LocationCreateRequest;
import com.example.weather.dto.location.LocationResponse;
import com.example.weather.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // 위치 등록 / 추가하기
    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @PathVariable Long userId,
            @RequestBody LocationCreateRequest request
    ) {
        LocationResponse response = locationService.createLocation(userId, request);
        return ResponseEntity
                .created(URI.create("/api/users/" + userId + "/locations/" + response.getId()))
                .body(response);
    }

    // 위치 목록 조회하기
    @GetMapping
    public ResponseEntity<List<LocationResponse>> getLocations(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(locationService.getLocations(userId));
    }

    // 위치 조회 (단건)
    @GetMapping("/{locationId}")
    public ResponseEntity<LocationResponse> getLocation(
            @PathVariable Long userId,
            @PathVariable Long locationId
    ) {
        return ResponseEntity.ok(locationService.getLocation(userId, locationId));
    }

    // 위치 삭제하기
    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable Long userId,
            @PathVariable Long locationId
    ) {
        locationService.deleteLocation(userId, locationId);
        return ResponseEntity.noContent().build();
    }
}
