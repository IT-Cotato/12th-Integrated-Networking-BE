package com.team3.backend.domain.dao;

import com.team3.backend.domain.entity.Location;
import com.team3.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByUser(User user);
}
