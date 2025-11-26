package cotato.backend.place.application.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PlaceListResponse {
    private final List<PlaceResponse> places;
    private final int count;

    public PlaceListResponse(List<PlaceResponse> places) {
        this.places = places;
        this.count = places.size();
    }
}
