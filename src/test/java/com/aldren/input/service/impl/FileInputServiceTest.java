package com.aldren.input.service.impl;

import com.aldren.input.service.InputService;
import com.aldren.properties.FileInputProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = FileInputProperties.class)
@ActiveProfiles("test")
public class FileInputServiceTest {

    @Autowired
    private FileInputProperties fileInputProperties;

    private InputService inputService;

    @Test
    public void testProcessInput() {
        inputService = new FileInputService(fileInputProperties);

        int expectedFileData = 1;
        int expectedFileDataLines = 8;

        Map<String, List<String>> lines = inputService.processInput();

        assertEquals(expectedFileData, lines.size());
        assertEquals(expectedFileDataLines, lines.entrySet()
                .stream()
                .findFirst()
                .get()
                .getValue()
                .size());
    }

    @Test
    public void testProcessInputNoFile() {
        fileInputProperties.setLocation("/testinput");
        inputService = new FileInputService(fileInputProperties);

        int expectedFileData = 0;

        Map<String, List<String>> lines = inputService.processInput();

        assertEquals(expectedFileData, lines.size());
    }

}
