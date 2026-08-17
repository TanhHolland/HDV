package nhom4.position.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nhom4.position.domain.core.root.DriverStatus;
import nhom4.position.domain.service.PositionService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/position")
public class PositionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PositionController.class);

    @Autowired
    PositionService positionService;

    @PostMapping("/update")
    public String positionUpdate(@RequestParam("driverId") Integer driverId,
                               @RequestParam("longitude") Double longitude,
                               @RequestParam("latitude") Double latitude) {
        if (!isValidCoordinates(longitude, latitude)){
            return "Toạ độ không hợp lệ";
        }
        LOGGER.info(String.format("Cập nhật vị trí: ID tài xế = %s, Kinh độ = %s, Vĩ độ = %s", driverId, longitude, latitude));
        return positionService.updatePosition(driverId, longitude, latitude);
    }

    @GetMapping("/match")
    public ResponseEntity<?> match(@RequestParam("longitude") Double longitude,
                                          @RequestParam("latitude") Double latitude) {
        if (!isValidCoordinates(longitude, latitude)){
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Tọa độ không hợp lệ");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        LOGGER.info(String.format("Tìm tài xế gần vị trí: Kinh độ = %s, Vĩ độ = %s", longitude, latitude));
        return new ResponseEntity<>(positionService.matchDriver(longitude, latitude), HttpStatus.OK);
    }
    private boolean isValidCoordinates(Double longitude, Double latitude){
        return  longitude != null && latitude != null &&
                longitude >= -180 && longitude <= 180 &&
                latitude >= -90 && latitude <= 90;
    }
}
