package com.example.weather.domain.location.service;

import com.example.weather.domain.location.entity.Location;
import com.example.weather.domain.user.entity.User;
import com.example.weather.domain.location.dto.LocationCreateRequest;
import com.example.weather.domain.location.dto.LocationResponse;
import com.example.weather.domain.location.repository.LocationRepository;
import com.example.weather.domain.user.repository.UserRepository;
import com.example.weather.global.exception.BusinessException;
import com.example.weather.global.error.ErrorCode;
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

    // ================================
    // 공통 조회 메서드 (중복 제거)
    // ================================
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Location getLocationOrThrow(User user, Long locationId) {
        return locationRepository.findByIdAndUser(locationId, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));
    }

    // ================================
    // 변환 메서드 (중복 제거)
    // ================================
    private LocationResponse toLocationResponse(Location loc) {
        return new LocationResponse(
                loc.getId(),
                loc.getUser().getId(),
                loc.getName(),
                loc.getLatitude(),
                loc.getLongitude(),
                loc.getCreatedAt()
        );
    }

    // ================================
    // CRUD 기능
    // ================================
    public LocationResponse createLocation(Long userId, LocationCreateRequest request) {
        User user = getUserOrThrow(userId);

        Location location = new Location(
                user,
                request.getName(),
                request.getLatitude(),
                request.getLongitude()
        );

        return toLocationResponse(locationRepository.save(location));
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> getLocations(Long userId) {
        User user = getUserOrThrow(userId);

        return locationRepository.findByUser(user).stream()
                .map(this::toLocationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LocationResponse getLocation(Long userId, Long locationId) {
        User user = getUserOrThrow(userId);
        Location loc = getLocationOrThrow(user, locationId);

        return toLocationResponse(loc);
    }

    public void deleteLocation(Long userId, Long locationId) {
        User user = getUserOrThrow(userId);
        Location loc = getLocationOrThrow(user, locationId);

        locationRepository.delete(loc);
    }
}
