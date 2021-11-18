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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = FileInputProperties.class)
@ActiveProfiles("test")
public class FileInputServiceImplTest {

    @Autowired
    private FileInputProperties fileInputProperties;

    private InputService inputService;

    @BeforeEach
    public void init() {
        inputService = new FileInputServiceImpl(fileInputProperties);
    }

    @Test
    public void testProcessInput() {
        int expectedFileData = 1;
        int expectedFileDataLines = 8;

        List<List<String>> lines = inputService.processInput();

        assertEquals(expectedFileData, lines.size());
        assertEquals(expectedFileDataLines, lines.get(0).size());
    }

}