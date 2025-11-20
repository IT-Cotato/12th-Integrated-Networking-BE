package com.team3.backend.api;

import com.team3.backend.domain.application.LocationService;
import com.team3.backend.domain.dto.request.CreateLocationRequest;
import com.team3.backend.domain.dto.request.PinRequest;
import com.team3.backend.domain.dto.response.LocationIdResponse;
import com.team3.backend.domain.dto.response.LocationListResponse;
import com.team3.backend.domain.entity.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/location")
    public LocationIdResponse createLocation(@RequestBody @Valid CreateLocationRequest req,
                                             HttpSession session) {

        User user = (User) session.getAttribute("LOGIN_USER");

        return locationService.createLocation(req,user);
    }

    @GetMapping("/location")
    public List<LocationListResponse> getLocationList(HttpSession session) {

        User user = (User) session.getAttribute("LOGIN_USER");
        return locationService.getLocationList(user);
    }

    @PatchMapping("/location/{id}/pin")
    public LocationIdResponse updatePin(@PathVariable("id") Long locationId, @RequestBody @Valid PinRequest req,
                          HttpSession session) {

        User user = (User) session.getAttribute("LOGIN_USER");

        return locationService.updatePin(req, user, locationId);
    }

    @DeleteMapping("/location/{id}")
    public LocationIdResponse deleteLocation(@PathVariable("id") Long locationId, HttpSession session) {

        User user = (User) session.getAttribute("LOGIN_USER");

        return locationService.deleteLocation(user, locationId);
    }

}
