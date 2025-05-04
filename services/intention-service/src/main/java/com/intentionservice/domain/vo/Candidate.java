package com.intentionservice.domain.vo;

import com.intentionservice.domain.root.Intention;
import lombok.Data;

import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "intention_candidate")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cid;
    @ManyToOne
    @JoinColumn(name = "intention_id")
    private Intention intention;
    private int driverId;
    private String driverName;
    private String driverMobile;
    private Double longitude;
    private Double latitude;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

}