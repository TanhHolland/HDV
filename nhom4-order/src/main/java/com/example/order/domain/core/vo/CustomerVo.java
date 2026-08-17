package com.example.order.domain.core.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CustomerVo {
    @Positive
    private int customerId;
    
    @NotBlank
    private String customerName;
    
    @NotBlank
    private String customerMobile;
}
