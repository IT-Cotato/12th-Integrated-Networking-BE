package cotato.backend.place.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceCreateRequest {
    private String placeName;
    private Double latitude;
    private Double longitude;
    private String address;
    private Boolean isPinned;
}
