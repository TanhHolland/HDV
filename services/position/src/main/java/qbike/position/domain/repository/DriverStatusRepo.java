package qbike.position.domain.repository;

import org.springframework.data.repository.CrudRepository;
import qbike.position.domain.core.root.DriverStatus;

public interface DriverStatusRepo extends CrudRepository<DriverStatus, Integer> {
    DriverStatus findByDriver_Id(Integer driverId);
}
