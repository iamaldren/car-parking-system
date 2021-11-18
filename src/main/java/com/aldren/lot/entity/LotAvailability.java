package com.aldren.lot.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;

@Getter
@Builder
@RedisHash("LotAvailability")
public class LotAvailability {

    @Id
    private String vehicleType;
    private Map<String, String> availableLots;

}
