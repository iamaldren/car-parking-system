package com.aldren.output.execution.scheduler;

import com.aldren.parking.service.ParkingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@ConditionalOnProperty(prefix = "app.system.output", name = "method", havingValue = "scheduler")
public class OutputScheduler {

    private final ParkingService parkingService;

    public OutputScheduler(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 5000)
    public void executeProcess() {
        parkingService.processInput();
    }

}
