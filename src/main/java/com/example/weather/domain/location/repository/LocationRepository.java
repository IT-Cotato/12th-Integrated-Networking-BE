package com.example.weather.domain.location.repository;

import com.example.weather.domain.location.entity.Location;
import com.example.weather.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByUser(User user);

    Optional<Location> findByIdAndUser(Long id, User user);
}
