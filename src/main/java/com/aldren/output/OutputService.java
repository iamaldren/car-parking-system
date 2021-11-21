package com.aldren.output;

import com.aldren.properties.FileOutputProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
@Service
@EnableConfigurationProperties(FileOutputProperties.class)
public class OutputService {

    private final FileOutputProperties fileOutputProperties;

    private Path outputFilePath;

    public OutputService(FileOutputProperties fileOutputProperties) {
        this.fileOutputProperties = fileOutputProperties;
    }

    @PostConstruct
    public void init() {
        if(fileOutputProperties.isEnabled()) {
            preparePath();
            prepareFile();
        }
    }

    public void writeOutput(String output) {
        System.out.println(output);

        if(fileOutputProperties.isEnabled() && Files.exists(outputFilePath)) {
            try {
                Files.writeString(outputFilePath, output + "\n", StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.error("Can't write to file", e);
            }
        }
     }

     private void preparePath() {
         String location = String.format("%1$s%2$s", System.getProperty("user.home"), fileOutputProperties.getLocation());
         if(!location.endsWith("/")) {
             location = String.format("%s/", location);
         }
         outputFilePath = Paths.get(String.format("%1$s%2$s", location, fileOutputProperties.getName()));
     }

    private void prepareFile() {
        try {
            Files.createDirectories(outputFilePath.getParent());
            Files.deleteIfExists(outputFilePath);
            Files.createFile(outputFilePath);
        } catch (IOException e) {
            log.error("Can't create file", e);
        }
    }

}
