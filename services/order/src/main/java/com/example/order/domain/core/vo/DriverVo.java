package com.example.order.domain.core.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DriverVo {
    @Positive
    private int driverId;
    
    @NotBlank
    private String driverName;
    
    @NotBlank
    private String driverMobile;
}
