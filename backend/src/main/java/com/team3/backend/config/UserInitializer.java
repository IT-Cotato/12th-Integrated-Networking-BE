package com.team3.backend.config;


import com.team3.backend.domain.dao.UserRepository;
import com.team3.backend.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UserInitializer {

    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            // 이메일을 기준으로 이미 유저가 존재하는지 확인하여 중복 등록 방지
            if (userRepository.findByEmail("test@test.com").isEmpty()) {
                // 테스트용 사용자 생성 (ID는 DB가 자동 생성하게 null로 둡니다.)
                User testUser = new User(null, "test@test.com", "hello");
                userRepository.save(testUser);
                System.out.println("--- Test User 'test@test.com' initialized and saved to DB. ---");
            } else {
                System.out.println("--- Test User 'test@test.com' already exists in DB. ---");
            }
        };
    }
}
