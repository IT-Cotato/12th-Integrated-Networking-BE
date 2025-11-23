package com.example.weather.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
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

}
