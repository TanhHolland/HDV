package com.example.order.domain.core.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class IntentionVo {
    @Positive
    private int customerId;
    
    @NotNull
    private double startLong;
    
    @NotNull
    private double startLat;
    
    @NotNull
    private double destLong;
    
    @NotNull
    private double destLat;
    
    @Positive
    private int intentionId;
    
    @Positive
    private int driverId;
}
