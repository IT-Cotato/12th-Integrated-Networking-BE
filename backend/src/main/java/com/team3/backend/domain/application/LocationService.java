package com.team3.backend.domain.application;

import com.team3.backend.domain.dao.LocationRepository;
import com.team3.backend.domain.dto.request.CreateLocationRequest;
import com.team3.backend.domain.dto.request.PinRequest;
import com.team3.backend.domain.dto.response.LocationIdResponse;
import com.team3.backend.domain.dto.response.LocationListResponse;
import com.team3.backend.domain.entity.Location;
import com.team3.backend.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional
    public LocationIdResponse createLocation(CreateLocationRequest req, User user) {

        Location location = Location.builder()
                .user(user)
                .placeName(req.getPlaceName())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .build();

        Location saved = locationRepository.save(location);

        return new LocationIdResponse(saved.getId());
    }

    @Transactional
    public List<LocationListResponse> getLocationList(User user) {

        List<Location> locations = locationRepository.findAllByUser(user);

        List<LocationListResponse> list = new ArrayList<>();

        for (Location location : locations) {

            LocationListResponse rep = LocationListResponse.builder()
                    .locationId(location.getId())
                    .placeName(location.getPlaceName())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .pinned(location.getPinned())
                    .build();

        list.add(rep);
        }

        return list;
    }

    @Transactional
    public LocationIdResponse updatePin(PinRequest req, User user, Long locationId) {

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 위치입니다."));

        if (!location.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인이 등록한 위치만 수정할 수 있습니다.");
        }

        location.updatePin(req.getPinned());

        return new LocationIdResponse(location.getId());
    }

    @Transactional
    public LocationIdResponse deleteLocation(User user, Long locationId) {

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 위치입니다."));

        if (!location.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인이 등록한 위치만 삭제할 수 있습니다.");
        }

        locationRepository.delete(location);

        return new LocationIdResponse(location.getId());
    }

}
