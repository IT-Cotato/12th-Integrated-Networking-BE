package com.team6.backend.domain.location.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team6.backend.domain.location.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
	Optional<Location> findByLocationId(Long locationId);
}
