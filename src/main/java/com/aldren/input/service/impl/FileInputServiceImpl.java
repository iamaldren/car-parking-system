package com.aldren.input.service.impl;

import com.aldren.input.service.InputService;
import com.aldren.properties.FileInputProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.input", name = "method", havingValue = "file")
public class FileInputServiceImpl implements InputService {

    private final FileInputProperties fileInputProperties;

    public FileInputServiceImpl(FileInputProperties fileInputProperties) {
        this.fileInputProperties = fileInputProperties;
    }

    @Override
    public List<List<String>> processInput() {
        return getInputFromClasspath();
    }

    private List<List<String>> getInputFromClasspath() {
        ClassLoader cl = this.getClass().getClassLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);

        try {
            return Arrays.stream(resolver.getResources(String.format("%s/*.txt", fileInputProperties.getLocation())))
                    .filter(Resource::exists)
                    .map(resource -> {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                            return reader.lines().collect(Collectors.toList());
                        } catch (IOException e) {
                            return new ArrayList<String>();
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("Failed to get file from resources directory {}; Will try getting from file system", fileInputProperties.getLocation());
            return getInputForFileSystem();
        }
    }

    private List<List<String>> getInputForFileSystem() {
        try (Stream<Path> files = Files.walk(Paths.get(fileInputProperties.getLocation()))) {
            List<String> filePaths = files.filter(Files::isRegularFile)
                    .map(path -> path.toString())
                    .collect(Collectors.toList());

            return filePaths.stream()
                    .map(path -> {
                        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8)) {
                            return stream.collect(Collectors.toList());
                        } catch (IOException e) {
                            log.error("Failed to get file from file system directory {}", fileInputProperties.getLocation(), e);
                            return new ArrayList<String>();
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to walk through files {}", fileInputProperties.getLocation(), e);
            return new ArrayList<>();
        }
    }

}
