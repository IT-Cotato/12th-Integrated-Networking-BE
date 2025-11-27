package cotato.backend.place.application.dto;

import cotato.backend.place.domain.Place;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PlaceResponse {
    private Long id;
    private String placeName;
    private Double latitude;
    private Double longitude;
    private String address;
    private Boolean isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PlaceResponse(Place place) {
        this.id = place.getId();
        this.placeName = place.getPlaceName();
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
        this.address = place.getAddress();
        this.isPinned = place.getIsPinned();
        this.createdAt = place.getCreatedAt();
        this.modifiedAt = place.getModifiedAt();
    }
}
