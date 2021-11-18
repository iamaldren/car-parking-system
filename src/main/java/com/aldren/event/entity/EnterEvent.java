package com.aldren.event.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;

@Getter
@Builder
@RedisHash("EnterEvent")
public class EnterEvent {

    @Id
    private String plateNumber;
    private String vehicle;
    private long timestamp;
    private String lot;
    private BigDecimal fee;

}
