package nhom4.position.domain.core.root;

import jakarta.persistence.*;
import lombok.Data;
import nhom4.position.domain.core.Status;
import nhom4.position.domain.core.vo.Driver;

import java.util.Date;

import static jakarta.persistence.EnumType.STRING;

@Data
@Entity
@Table(name="t_driver_status")
public class DriverStatus {
    @Id
    private int dId;
    @Embedded
    private Driver driver;
    private Double currentLongitude;
    private Double currentLatitude;
    @Enumerated(value = STRING)
    @Column(length = 32,nullable = false)
    private Status status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
}
