package com.aldren.output.execution.controller;

import com.aldren.parking.service.ParkingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@ConditionalOnProperty(prefix = "app.system.output", name = "method", havingValue = "api")
public class OutputController {

    private final ParkingService parkingService;

    public OutputController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping("/parking")
    public void executeParkingService() {
        this.parkingService.processInput();
    }

}
