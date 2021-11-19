package com.aldren.event.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;

@Getter
@Builder
@RedisHash("ExitEvent")
public class ExitEvent {

    @Id
    private String plateNumber;
    private String vehicle;
    private String lot;
    private long timestamp;
    private BigDecimal totalFee;

}
