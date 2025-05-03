package qbike.position.domain.core.root;

import lombok.Data;
import qbike.position.domain.core.Status;
import qbike.position.domain.core.vo.Driver;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.EnumType.STRING;

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
