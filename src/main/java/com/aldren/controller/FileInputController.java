package com.aldren.controller;

import com.aldren.input.service.InputService;
import com.aldren.parking.service.ParkingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FileInputController {

    private final ParkingService parkingService;

    public FileInputController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping("/parking")
    public void executeParkingService() {
        this.parkingService.processInput();
    }

}
