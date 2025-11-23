package com.team3.backend.api;

import com.team3.backend.domain.application.UserService;
import com.team3.backend.domain.dao.UserRepository;
import com.team3.backend.domain.dto.request.LoginRequest;
import com.team3.backend.domain.dto.request.SignUpRequest;
import com.team3.backend.domain.dto.response.ErrorResponse;
import com.team3.backend.domain.dto.response.UserResponse;
import com.team3.backend.domain.entity.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    public static final String LOGIN_USER = "LOGIN_USER";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp
    (@RequestBody @Valid SignUpRequest req
    ) {
        return ResponseEntity.ok(
                userService.signUp(req)
        );
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequest req,
            HttpSession session
    ) {
        Optional<User> optional = userRepository.findByEmail(req.getEmail());

        if (optional.isEmpty()) {
            return ResponseEntity
                    .status(400)
                    .body(ErrorResponse.of("EMAIL_NOT_FOUND", "존재하지 않는 이메일입니다."));
        }

        User user = optional.get();

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(401)
                    .body(ErrorResponse.of("PASSWORD_NOT_MATCH", "비밀번호가 일치하지 않습니다."));
        }


        // 세션 생성 및 값 저장
        session.setAttribute(LOGIN_USER, user.getId());

        return ResponseEntity.ok(
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    // 로그아웃
    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
