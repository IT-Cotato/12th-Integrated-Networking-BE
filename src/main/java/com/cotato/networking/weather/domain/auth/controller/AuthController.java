package com.cotato.networking.weather.domain.auth.controller;

import com.cotato.networking.weather.domain.auth.converter.AuthConverter;
import com.cotato.networking.weather.domain.auth.dto.request.AuthLoginRequestDto;
import com.cotato.networking.weather.domain.auth.dto.request.AuthSignUpRequestDto;
import com.cotato.networking.weather.domain.auth.dto.response.AuthResponseDto;
import com.cotato.networking.weather.domain.auth.service.AuthService;
import com.cotato.networking.weather.domain.user.entity.User;
import com.cotato.networking.weather.global.config.AuthInterceptor;
import com.cotato.networking.weather.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthConverter authConverter;

    /**
     * 회원 가입 API
     */
    @PostMapping("/signup")
    public ApiResponse<AuthResponseDto> signup(@RequestBody AuthSignUpRequestDto requestDto) {
        User newUser = authService.signUp(requestDto);
        AuthResponseDto responseDto = authConverter.toDto(newUser);

        return ApiResponse.onSuccess(responseDto);
    }

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody AuthLoginRequestDto requestDto, HttpServletRequest request) {
        User user = authService.authenticate(requestDto);

        // 세션 생성 및 ID 저장
        HttpSession session = request.getSession();
        session.setAttribute(AuthInterceptor.SESSION_USER_ID, user.getId());

        return ApiResponse.onSuccess("로그인 성공");
    }

    /**
     * 로그아웃 API
     */
    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ApiResponse.onSuccess("로그아웃 성공");
    }
}
