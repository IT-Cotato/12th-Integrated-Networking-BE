package com.example.weather.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id")
    private Long id;

    private String username;
    private String role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public User(String username, String role) {
        this.username = username;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }
}
