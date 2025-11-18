package com.cotato.networking.weather.domain.auth.service;

import com.cotato.networking.weather.domain.auth.converter.AuthConverter;
import com.cotato.networking.weather.domain.auth.dto.request.AuthLoginRequestDto;
import com.cotato.networking.weather.domain.auth.dto.request.AuthSignUpRequestDto;
import com.cotato.networking.weather.domain.user.entity.User;
import com.cotato.networking.weather.domain.user.repository.UserRepository;
import com.cotato.networking.weather.global.exception.ErrorStatus;
import com.cotato.networking.weather.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthConverter authConverter;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * @param dto 회원가입 요청 DTO
     * @return 가입된 사용자 정보
     */
    @Transactional
    public User signUp(AuthSignUpRequestDto dto) {
        // 중복된 username 검사
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new GeneralException(ErrorStatus.USER_EXISTS);
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 암호화된 비밀번호를 컨버터에 전달
        User user = authConverter.toEntity(dto, encodedPassword);

        return userRepository.save(user);
    }

    /**
     * 로그인 (인증)
     * @return 인증된 사용자 정보
     */
    @Transactional(readOnly = true)
    public User authenticate(AuthLoginRequestDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 비밀번호 비교 (matches(평문, 암호화된값))
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.USER_PASSWORD_MISMATCH);
        }

        return user;
    }
}
