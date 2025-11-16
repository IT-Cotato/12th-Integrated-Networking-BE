package com.cotato.networking.weather.domain.test.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cotato.networking.weather.domain.test.entity.Test;

public interface TestRepository extends JpaRepository<Test, Long> {
}
