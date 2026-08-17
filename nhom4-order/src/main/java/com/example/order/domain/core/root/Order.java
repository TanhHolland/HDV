package com.example.order.domain.core.root;


import com.example.order.domain.core.vo.CustomerVo;
import com.example.order.domain.core.vo.DriverVo;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Data
@ToString
@Accessors(fluent = false, chain = true)
@Entity
@Table(name = "t_nhom4_order")
public class Order {
    @Id
    @GeneratedValue(generator = "order-id")
    @GenericGenerator(name = "order-id", strategy = "org.hibernate.id.UUIDGenerator")
    private String oid;
    
    @Valid
    @Embedded
    private CustomerVo customer;
    
    @Valid
    @Embedded
    private DriverVo driver;
    
    @NotNull
    private Double startLong;
    
    @NotNull
    private Double startLat;
    
    @NotNull
    private Double destLong;
    
    @NotNull
    private Double destLat;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date opened;
    
    @Column(length = 32, nullable = false)
    private String orderStatus;
    
    private String intentionId;
}
