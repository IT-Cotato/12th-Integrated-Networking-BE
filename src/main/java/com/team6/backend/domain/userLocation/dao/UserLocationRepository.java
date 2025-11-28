package com.team6.backend.domain.userLocation.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team6.backend.domain.location.entity.Location;
import com.team6.backend.domain.user.entity.User;
import com.team6.backend.domain.userLocation.entity.UserLocation;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
	Optional<UserLocation> findByUserAndLocation(User user, Location location);

	List<UserLocation> findByUserOrderByPinnedDesc(User user);
}
