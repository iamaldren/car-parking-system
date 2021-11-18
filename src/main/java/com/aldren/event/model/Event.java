package com.aldren.event.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Event {

    private String event;
    private String vehicleType;
    private String plateNumber;
    private long timestamp;

}
