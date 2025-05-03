package com.example.order.domain.core.root;


import com.example.order.domain.core.vo.CustomerVo;
import com.example.order.domain.core.vo.DriverVo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Data
@ToString
@Accessors(fluent = false, chain = true)
@Entity
@Table(name = "t_qbike_order")
public class Order {
    @Id
    @GeneratedValue(generator = "order-id")
    @GenericGenerator(name = "order-id", strategy = "org.hibernate.id.UUIDGenerator")
    private String oid;
    @Embedded
    private CustomerVo customer;
    @Embedded
    private DriverVo driver;
    private Double startLong;
    private Double startLat;
    private Double destLong;
    private Double destLat;
    @Temporal(TemporalType.TIMESTAMP)
    private Date opened;
    @Column(length = 32, nullable = false)
    private String orderStatus;
    private String intentionId;

}
