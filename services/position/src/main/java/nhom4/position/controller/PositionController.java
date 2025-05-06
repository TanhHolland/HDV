package nhom4.position.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import nhom4.position.domain.core.root.DriverStatus;
import nhom4.position.domain.service.PositionService;

import java.util.Collection;

@RestController
@RequestMapping("/api/position")
public class PositionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PositionController.class);

    @Autowired
    PositionService positionService;

    @PostMapping("/update")
    public void positionUpdate(@RequestParam("driverId") Integer driverId,
                               @RequestParam("longitude") Double longitude,
                               @RequestParam("latitude") Double latitude) {
        LOGGER.info(String.format("Cập nhật vị trí: ID tài xế = %s, Kinh độ = %s, Vĩ độ = %s", driverId, longitude, latitude));
        positionService.updatePosition(driverId, longitude, latitude);
    }

    @GetMapping("/match")
    public Collection<DriverStatus> match(@RequestParam("longitude") Double longitude,
                                          @RequestParam("latitude") Double latitude) {
        LOGGER.info(String.format("Tìm tài xế gần vị trí: Kinh độ = %s, Vĩ độ = %s", longitude, latitude));
        return positionService.matchDriver(longitude, latitude);
    }
}
