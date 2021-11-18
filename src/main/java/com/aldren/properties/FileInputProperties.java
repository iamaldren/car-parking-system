package com.aldren.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("app.system.input.file")
public class FileInputProperties {

    private String location;

}
