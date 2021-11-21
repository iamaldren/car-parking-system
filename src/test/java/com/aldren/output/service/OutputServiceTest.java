package com.aldren.output.service;

import com.aldren.output.OutputService;
import com.aldren.properties.FileOutputProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = FileOutputProperties.class)
@ActiveProfiles("test")
public class OutputServiceTest {

    @Autowired
    private FileOutputProperties fileOutputProperties;

    @TempDir
    Path tempDir;

    private Path outputFilePath;

    private OutputService outputService;

    @BeforeEach
    public void init() {
        outputFilePath = tempDir.resolve(fileOutputProperties.getName());

        outputService = new OutputService(fileOutputProperties);
        ReflectionTestUtils.setField(outputService, "outputFilePath", outputFilePath);
        ReflectionTestUtils.invokeMethod(outputService, "prepareFile");
    }

    @Test
    public void processOutputTest() throws IOException {
        String sampleOutput = "Test";

        outputService.writeOutput(sampleOutput);

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add(sampleOutput);

        assertTrue(Files.exists(outputFilePath));
        assertLinesMatch(expectedLines, Files.readAllLines(outputFilePath));
    }

}
