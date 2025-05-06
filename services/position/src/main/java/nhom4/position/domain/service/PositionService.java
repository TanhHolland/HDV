package nhom4.position.domain.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import nhom4.position.domain.core.Status;
import nhom4.position.domain.core.root.DriverStatus;
import nhom4.position.domain.core.vo.Driver;
import nhom4.position.domain.core.vo.Position;
import nhom4.position.domain.repository.DriverStatusRepo;
import nhom4.position.domain.repository.PositionRepository;
import nhom4.position.infrastructure.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class PositionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PositionService.class);

    @Autowired
    DriverStatusRepo driverStatusRepo;

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    PositionRepository positionRepository;

    public String updatePosition(Integer driverId, Double longitude, Double latitude){

        //ghi lai du lieu
        Date current = new Date();
        Position position = new Position();
        position.setDriverId(String.valueOf(driverId));
        position.setPositionLongitude(longitude);
        position.setPositionLatitude(latitude);

        position.setStatus(Status.ONLINE);
        position.setUploadTime(current);
        positionRepository.save(position);

        //Cap nhat tai xe
        DriverStatus driverStatus = driverStatusRepo.findByDriver_Id(driverId);
        if(driverStatus != null){
            driverStatus.setCurrentLongitude(longitude);
            driverStatus.setCurrentLatitude(latitude);
            driverStatus.setUpdateTime(current);
            driverStatusRepo.save(driverStatus);
        }
        else {
            Driver driver = userService.findById(driverId);
            driverStatus = new DriverStatus();
            driverStatus.setDriver(driver);
            driverStatus.setCurrentLongitude(longitude);
            driverStatus.setCurrentLatitude(latitude);
            driverStatus.setUpdateTime(current);
            driverStatus.setStatus(Status.ONLINE);
            driverStatus.setDId(driverId);
            driverStatusRepo.save(driverStatus);
        }

        //Cap nhat vao Redis
        redisTemplate.opsForGeo().add("Driver", new Point(longitude,latitude), String.valueOf(driverId));
        return ("Da cap nhat vi tri moi: " + driverStatus);
    }

    public Collection<DriverStatus> matchDriver(double longtitude, double latitude){
        Circle circle = new Circle(new Point(longtitude,latitude),
                new Distance(500, RedisGeoCommands.DistanceUnit.METERS));
        GeoResults<RedisGeoCommands.GeoLocation<String>> result =
                redisTemplate.opsForGeo().radius("Driver", circle);

        if(result.getContent().size() == 0){
            LOGGER.info("Khong tim thay tai xe");
            return  new ArrayList<>();
        }
        else {
            List<String> drivers = result.getContent().stream().map(x -> x.getContent().getName()).collect(toList());
            LOGGER.info("Tim thay danh sach tai xe {}", drivers);
            return drivers.stream().map(Integer::parseInt).map(id -> driverStatusRepo.findByDriver_Id(id)).collect(toList());
        }
    }


}
