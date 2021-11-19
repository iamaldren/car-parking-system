package com.aldren.input.execution.controller;

import com.aldren.parking.service.ParkingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@ConditionalOnProperty(prefix = "app.system.run", name = "method", havingValue = "api")
public class InputController {

    private final ParkingService parkingService;

    public InputController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping("/parking")
    public void executeParkingService() {
        this.parkingService.processInput();
    }

}
