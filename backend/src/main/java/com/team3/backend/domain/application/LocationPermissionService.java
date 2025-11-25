package com.team3.backend.domain.application;

import com.team3.backend.domain.dao.LocationRepository;
import com.team3.backend.domain.entity.Location;
import com.team3.backend.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.team3.backend.global.exception.LocationNotFoundException;
import com.team3.backend.global.exception.ForbiddenLocationException;


@Service
@RequiredArgsConstructor
public class LocationPermissionService {

    private final LocationRepository locationRepository;

    public Location getAuthorizedLocation(User user, Long locationId) {

        Location location = locationRepository.findById(locationId)
                .orElseThrow(LocationNotFoundException::new);

        if (!location.getUser().getId().equals(user.getId())) {
            throw new ForbiddenLocationException();
        }
        return location;
    }

}
