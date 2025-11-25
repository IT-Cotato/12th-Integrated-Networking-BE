package cotato.backend.place.api;

import cotato.backend.place.application.PlaceService;
import cotato.backend.place.application.dto.PlaceCreateRequest;
import cotato.backend.place.application.dto.PlaceListResponse;
import cotato.backend.place.application.dto.PlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cotato/backend/place/locations")
public class PlaceController {
    private final PlaceService placeService;

    // POST /locations 위치 저장
    @PostMapping
    public ResponseEntity<PlaceResponse> createPlace(@RequestBody PlaceCreateRequest request) {
        PlaceResponse response = placeService.createPlace(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /locations 저장된 장소 목록 조회
    @GetMapping
    public ResponseEntity<PlaceListResponse> findAllPlaces() {
        PlaceListResponse response = placeService.findAllPlaces();
        return ResponseEntity.ok(response);
    }

    // DELETE /locations/{locationID} 저장된 장소 삭제
    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long locationId) {
        placeService.deletePlace(locationId);
        return ResponseEntity.noContent().build();
    }
}
