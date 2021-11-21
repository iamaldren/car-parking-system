package com.aldren.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("app.system.output.file")
public class FileOutputProperties {

    private String location;
    private String name;
    private boolean enabled;

}
