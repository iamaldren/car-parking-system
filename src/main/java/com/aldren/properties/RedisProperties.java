package com.aldren.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties("app.redis")
public class RedisProperties {

    private String host;
    private int port;
    private String password;

}
