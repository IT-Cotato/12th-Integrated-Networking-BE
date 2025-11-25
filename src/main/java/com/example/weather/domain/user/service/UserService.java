package com.example.weather.domain.user.service;

import com.example.weather.domain.user.dto.UserCreateRequest;
import com.example.weather.domain.user.dto.UserResponse;
import com.example.weather.domain.user.entity.User;
import com.example.weather.domain.user.repository.UserRepository;
import com.example.weather.global.error.ErrorCode;
import com.example.weather.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ================================
    // 공통 조회 메서드 (중복 제거)
    // ================================
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    // ================================
    // 변환 메서드 (중복 제거)
    // ================================
    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    // ================================
    // CRUD 기능
    // ================================
    public UserResponse createUser(UserCreateRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .role(request.getRole())
                .build();

        return toUserResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        User user = getUserOrThrow(userId);
        return toUserResponse(user);
    }

    public void deleteUser(Long userId) {
        User user = getUserOrThrow(userId);
        userRepository.delete(user);
    }
}
