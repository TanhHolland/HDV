package nhom4.position.domain.core.vo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import nhom4.position.domain.core.Status;

import jakarta.persistence.*;

import java.util.Date;

import static jakarta.persistence.EnumType.STRING;

@Data
@ToString
@Accessors(fluent = false, chain = true)
@Entity
@Table(name = "t_position")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tid;
    private Double positionLongitude;
    private Double positionLatitude;
    @Enumerated(value = STRING)
    @Column(length = 32, nullable = false)
    private Status status;
    private String driverId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadTime;
}
