package com.team3.backend.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.team3.backend.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}