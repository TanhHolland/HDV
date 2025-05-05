package com.intentionservice.domain.vo;

import lombok.Data;

import jakarta.persistence.Embeddable;

@Embeddable
@Data
public class Customer
{
    private int customerId;
    private String customerName;
    private String customerMobile;
    private String userType;
}