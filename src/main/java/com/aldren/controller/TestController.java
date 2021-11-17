package com.aldren.controller;

import com.aldren.input.service.InputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private InputService inputService;

    @GetMapping("/test")
    public void test() throws URISyntaxException {
        inputService.processInput().forEach(System.out::println);
    }

}
