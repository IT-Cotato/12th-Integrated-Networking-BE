package com.example.weather.service;

import com.example.weather.domain.Location;
import com.example.weather.domain.User;
import com.example.weather.dto.location.LocationCreateRequest;
import com.example.weather.dto.location.LocationResponse;
import com.example.weather.repository.LocationRepository;
import com.example.weather.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LocationService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public LocationService(UserRepository userRepository,
                           LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    public LocationResponse createLocation(Long userId, LocationCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Location location = new Location(
                user,
                request.getName(),
                request.getLatitude(),
                request.getLongitude()
        );

        Location saved = locationRepository.save(location);

        return new LocationResponse(
                saved.getId(),
                user.getId(),
                saved.getName(),
                saved.getLatitude(),
                saved.getLongitude(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> getLocations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return locationRepository.findByUser(user).stream()
                .map(loc -> new LocationResponse(
                        loc.getId(),
                        user.getId(),
                        loc.getName(),
                        loc.getLatitude(),
                        loc.getLongitude(),
                        loc.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public LocationResponse getLocation(Long userId, Long locationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Location loc = locationRepository.findByIdAndUser(locationId, user)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        return new LocationResponse(
                loc.getId(),
                user.getId(),
                loc.getName(),
                loc.getLatitude(),
                loc.getLongitude(),
                loc.getCreatedAt()
        );
    }

    public void deleteLocation(Long userId, Long locationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Location loc = locationRepository.findByIdAndUser(locationId, user)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        locationRepository.delete(loc);
    }
}
