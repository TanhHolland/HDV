package com.example.order.domain.core.vo;

import java.util.HashMap;
import java.util.Map;

public enum FlowState {
    WAITING_ABOARD("WAITING_ABOARD", "Waiting for boarding"),
    WAITING_ARRIVE("WAITING_ARRIVE", "Waiting to arrive at destination"),
    UNPAY("UNPAY", "Waiting for payment"),
    PAYING("PAYING", "Payment submitted"),
    CANCELED("CANCELED", "Order canceled");
    private static Map<String, FlowState> flowStateMap = new HashMap<>();

    static {
        flowStateMap.put(WAITING_ABOARD.getStateId(), WAITING_ABOARD);
        flowStateMap.put(WAITING_ARRIVE.getStateId(), WAITING_ARRIVE);
        flowStateMap.put(UNPAY.getStateId(), UNPAY);
        flowStateMap.put(PAYING.getStateId(), PAYING);
        flowStateMap.put(CANCELED.getStateId(), CANCELED);
    }

    private String stateId;
    private String description;

    FlowState(String stateId, String description) {
        this.stateId = stateId;
        this.description = description;
    }

    public static FlowState forValue(String stateId) {
        return flowStateMap.get(stateId);
    }

    public String toValue() {
        return this.getStateId();
    }

    public String getStateId() {
        return stateId;
    }

    public String getDescription() {
        return description;
    }
}
