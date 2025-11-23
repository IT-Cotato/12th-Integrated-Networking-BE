package com.team3.backend.api;

import com.team3.backend.domain.application.LocationService;
import com.team3.backend.domain.dao.UserRepository;
import com.team3.backend.domain.dto.request.CreateLocationRequest;
import com.team3.backend.domain.dto.request.PinRequest;
import com.team3.backend.domain.dto.response.ApiResponse;
import com.team3.backend.domain.dto.response.LocationIdResponse;
import com.team3.backend.domain.dto.response.LocationListResponse;
import com.team3.backend.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Location API", description = "장소 생성, 조회, 고정, 삭제 기능 제공")
public class LocationController {

    private final LocationService locationService;
    private final UserRepository userRepository;

    // 테스트 유저를 DB에서 조회 (임시)
    private User getTestUserFromDb() {
        // 초기화 시 등록한 이메일로 DB에서 유저를 조회합니다.
        Optional<User> userOptional = userRepository.findByEmail("test@test.com");

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Test user 'test@test.com' not found. Please check UserInitializer.");
        }
        return userOptional.get();
    }

    @Operation(summary = "장소 생성", description = "사용자가 새로운 장소를 등록합니다.")
    @PostMapping("/location")
    public ApiResponse<LocationIdResponse> createLocation(
            @RequestBody @Valid CreateLocationRequest req,
            HttpSession session) {

        User user = getTestUserFromDb();
        LocationIdResponse result = locationService.createLocation(req, user);

        return ApiResponse.<LocationIdResponse>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(result)
                .build();
    }

    @Operation(summary = "장소 목록 조회", description = "사용자가 등록한 모든 장소 목록을 반환합니다. (고정 상태 포함)")
    @GetMapping("/location")
    public ApiResponse<List<LocationListResponse>> getLocationList(HttpSession session) {

        User user = getTestUserFromDb();
        List<LocationListResponse> result = locationService.getLocationList(user);

        return ApiResponse.<List<LocationListResponse>>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(result)
                .build();
    }

    @Operation(summary = "장소 고정 상태 변경", description = "특정 장소의 고정(pin) 상태를 업데이트합니다.")
    @PatchMapping("/location/{id}/pin")
    public ApiResponse<LocationIdResponse> updatePin(
            @PathVariable("id") Long locationId,
            @RequestBody @Valid PinRequest req,
            HttpSession session) {

        User user = getTestUserFromDb();

        LocationIdResponse result = locationService.updatePin(req, user, locationId);

        return ApiResponse.<LocationIdResponse>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(result)
                .build();
    }

    @Operation(summary = "장소 삭제", description = "특정 장소를 목록에서 삭제합니다.")
    @DeleteMapping("/location/{id}")
    public ApiResponse<LocationIdResponse> deleteLocation(
            @PathVariable("id") Long locationId,
            HttpSession session) {


        User user = getTestUserFromDb();

        LocationIdResponse result = locationService.deleteLocation(user, locationId);
        return ApiResponse.<LocationIdResponse>builder()
                .code("REQUEST_OK")
                .message("request succeeded")
                .success(true)
                .results(result)
                .build();
    }
}
