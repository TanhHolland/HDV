package com.intentionservice.controller.bean;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;


@ToString
@Data
@Accessors(fluent = false, chain = true)
public class IntentionVo
{
    private int customerId;
    private double startLong;
    private double startLat;
    private double destLong;
    private double destLat;
    private int intentionId;
    private int driverId;

    public IntentionVo() {
    }

    public IntentionVo(int customerId, double startLong, double startLat, double destLong, double destLat, int intentionId, int driverId) {
        this.customerId = customerId;
        this.startLong = startLong;
        this.startLat = startLat;
        this.destLong = destLong;
        this.destLat = destLat;
        this.intentionId = intentionId;
        this.driverId = driverId;
    }
}