package com.intentionservice.domain.root;

import com.intentionservice.domain.vo.Candidate;
import com.intentionservice.domain.vo.Customer;
import com.intentionservice.domain.vo.DriverVo;
import com.intentionservice.domain.vo.IntentionStatus;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;

@Data
@ToString
@Accessors(fluent = false, chain = true)
@Entity
@Table(name = "intention_intention")
public class Intention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mid;
    private Double startLongitude;
    private Double startLatitude;
    private Double destLongitude;
    private Double destLatitude;
    @Embedded
    private Customer customer;
    @Enumerated(value = STRING)
    @Column(length = 32, nullable = false)
    private IntentionStatus status;
    @Embedded
    private DriverVo selectedDriver;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated = new Date();

    @OneToMany(mappedBy = "intention", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Candidate> candidates = new ArrayList<>();

    public boolean canMatchDriver() {
        return status == IntentionStatus.Inited;
    }

    private boolean canConfirm() {
        return status == IntentionStatus.Unconfirmed;
    }

    public boolean waitingConfirm() {
        if (canMatchDriver()) {
            this.status = IntentionStatus.Unconfirmed;
            this.updated = new Date();
            return true;
        } else {
            return false;
        }
    }

    public boolean fail() {
        if (this.status == IntentionStatus.Inited) {
            this.status = IntentionStatus.Failed;
            this.updated = new Date();
            return true;
        } else {
            return false;
        }
    }

    public int confirmIntention(DriverVo driverVo) {
        if (!canConfirm()) {
            return -1;
        }
        if (candidates.stream().map(Candidate::getDriverId).noneMatch(id -> id == driverVo.getId())) {
            return -2;
        }
        if (this.selectedDriver == null) {
            this.selectedDriver = driverVo;
            this.status = IntentionStatus.Confirmed;
            this.updated = new Date();
            return 0;
        } else {
            return -3;
        }

    }
}