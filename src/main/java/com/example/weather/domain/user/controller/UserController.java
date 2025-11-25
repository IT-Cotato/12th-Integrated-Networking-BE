package com.example.weather.domain.user.controller;

import com.example.weather.domain.user.dto.UserCreateRequest;
import com.example.weather.domain.user.dto.UserResponse;
import com.example.weather.domain.user.service.UserService;
import com.example.weather.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "사용자 등록",
            description = "새로운 사용자를 등록합니다."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> createUser(
            @RequestBody UserCreateRequest request
    ) {
        UserResponse response = userService.createUser(request);
        return ApiResponse.success(response);
    }

    @Operation(
            summary = "사용자 목록 조회",
            description = "모든 사용자 목록을 조회합니다."
    )
    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers() {
        List<UserResponse> users = userService.getUsers();
        return ApiResponse.success(users);
    }

    @Operation(
            summary = "사용자 단건 조회",
            description = "특정 사용자를 조회합니다."
    )
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(
            @PathVariable Long userId
    ) {
        UserResponse user = userService.getUser(userId);
        return ApiResponse.success(user);
    }

    @Operation(
            summary = "사용자 삭제",
            description = "특정 사용자를 삭제합니다."
    )
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteUser(
            @PathVariable Long userId
    ) {
        userService.deleteUser(userId);
        return ApiResponse.success();
    }
}
