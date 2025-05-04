package com.intentionservice.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Embeddable
public class DriverVo {
    @Column(nullable = true)
    private int id;
    @Column(nullable = true)
    private String userName;
    @Column(nullable = true)
    private String mobile;
    @Column(nullable = true)
    private String type;
}