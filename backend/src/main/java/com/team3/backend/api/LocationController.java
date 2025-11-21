package com.team3.backend.api;

import com.team3.backend.domain.application.LocationService;
import com.team3.backend.domain.dto.request.CreateLocationRequest;
import com.team3.backend.domain.dto.request.PinRequest;
import com.team3.backend.domain.dto.response.ApiResponse;
import com.team3.backend.domain.dto.response.LocationIdResponse;
import com.team3.backend.domain.dto.response.LocationListResponse;
import com.team3.backend.domain.entity.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/location")
    public ApiResponse<LocationIdResponse> createLocation(
            @RequestBody @Valid CreateLocationRequest req,
            HttpSession session) {

        //User user = (User) session.getAttribute("LOGIN_USER");
        //임시
        User user = new User(1L, "test@test.com,","hello");
        LocationIdResponse result = locationService.createLocation(req, user);

        return ApiResponse.<LocationIdResponse>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(result)
                .build();
    }

    @GetMapping("/location")
    public ApiResponse<List<LocationListResponse>> getLocationList(HttpSession session) {

        //User user = (User) session.getAttribute("LOGIN_USER");
        //임시
        User user = new User(1L, "test@test.com,","hello");
        List<LocationListResponse> result = locationService.getLocationList(user);

        return ApiResponse.<List<LocationListResponse>>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(result)
                .build();
    }

    @PatchMapping("/location/{id}/pin")
    public ApiResponse<LocationIdResponse> updatePin(
            @PathVariable("id") Long locationId,
            @RequestBody @Valid PinRequest req,
            HttpSession session) {

        //User user = (User) session.getAttribute("LOGIN_USER");
        //임시
        User user = new User(1L, "test@test.com,","hello");
        LocationIdResponse result = locationService.updatePin(req, user, locationId);

        return ApiResponse.<LocationIdResponse>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(result)
                .build();
    }

    @DeleteMapping("/location/{id}")
    public ApiResponse<LocationIdResponse> deleteLocation(
            @PathVariable("id") Long locationId,
            HttpSession session) {

        //User user = (User) session.getAttribute("LOGIN_USER");
        //임시
        User user = new User(1L, "test@test.com,","hello");
        LocationIdResponse result = locationService.deleteLocation(user, locationId);

        return ApiResponse.<LocationIdResponse>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(result)
                .build();
    }
}
