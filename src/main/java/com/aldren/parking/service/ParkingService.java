package com.aldren.parking.service;

import com.aldren.event.model.Event;
import com.aldren.event.service.impl.EnterEventService;
import com.aldren.event.service.impl.ExitEventService;
import com.aldren.input.service.InputService;
import com.aldren.lot.service.LotService;
import com.aldren.output.OutputService;
import com.aldren.properties.VehicleProperties;
import com.aldren.util.ErrorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@EnableConfigurationProperties(VehicleProperties.class)
public class ParkingService {

    private final InputService inputService;
    private final EnterEventService enterEventService;
    private final ExitEventService exitEventService;
    private final LotService lotService;
    private final VehicleProperties vehicleProperties;
    private final OutputService outputService;

    private static final String EVENT_ENTER = "Enter";
    private static final String EVENT_EXIT = "Exit";

    public ParkingService(InputService inputService,
                          EnterEventService enterEventService,
                          ExitEventService exitEventService,
                          LotService lotService,
                          VehicleProperties vehicleProperties,
                          OutputService outputService) {
        this.inputService = inputService;
        this.enterEventService = enterEventService;
        this.exitEventService = exitEventService;
        this.lotService = lotService;
        this.vehicleProperties = vehicleProperties;
        this.outputService = outputService;
    }

    public void processInput() {
        inputService.processInput()
                .entrySet()
                .stream()
                .map(mapData -> {
                    log.info("Processing file {}", mapData.getKey());
                    if(outputService.isFileOutputEnabled()) {
                        outputService.prepareFile(mapData.getKey());
                    }
                    return mapData.getValue();
                })
                .filter(data -> isAvailableLotsSet(data.get(0)))
                .forEach(data -> {
                    processEachFile(data);
                });
    }

    private boolean isAvailableLotsSet(String lotCount) {
        String[] availableLots = lotCount.trim().split("\\s+");

        if(availableLots.length != vehicleProperties.getTypes().size()) {
            outputService.writeOutput(String.format("%1$s Skipping file, wrong format for lot count [%2$s].", ErrorUtil.ERROR_BAD_DATA, lotCount));
            return false;
        }

        if(Arrays.stream(availableLots)
                .filter(lot -> isDataNumeric(lot))
                .collect(Collectors.toList())
                .size() != vehicleProperties.getTypes().size()) {
            outputService.writeOutput(String.format("%1$s Skipping file, lot count is not numeric [%2$s %3$s].", ErrorUtil.ERROR_BAD_DATA, availableLots[0], availableLots[1]));
            return false;
        }

        IntStream.range(0, availableLots.length)
                .forEach(i -> {
                    String vehicleType = vehicleProperties.getKindByIndex().get(i);
                    lotService.setAvailableLots(vehicleType, vehicleProperties.getLotName().get(vehicleType), Integer.parseInt(availableLots[i]));
                });

        return true;
    }

    private boolean isDataNumeric(String data) {
        try {
            Integer.parseInt(data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void processEachFile(List<String> data) {
        data.stream()
                .skip(1)
                .forEach(this::parseDataToEvent);

        cleanUp();
    }

    private void parseDataToEvent(String data) {
        String[] splitData = data.trim().split("\\s+");

        switch(splitData.length) {
            case 3:
                if(EVENT_EXIT.equals(splitData[0])) {
                    processExitEvent(splitData);
                    break;
                }
            case 4:
                if(EVENT_ENTER.equals(splitData[0])) {
                    processEnterEvent(splitData);
                    break;
                }
            default:
                outputService.writeOutput(String.format("%1$s Skipping event, wrong format. Recognized events are [ENTER, EXIT]. Data length is expected to be 4 for ENTER and 3 for EXIT. [%2$s]", ErrorUtil.ERROR_BAD_DATA, data));
        }
    }

    private void processEnterEvent(String[] data) {
        Event event = Event.builder()
                .event(data[0])
                .vehicleType(data[1])
                .plateNumber(data[2])
                .timestamp(Long.valueOf(data[3]))
                .build();

        outputService.writeOutput(enterEventService.processEvent(event));
    }

    private void processExitEvent(String[] data) {
        Event event = Event.builder()
                .event(data[0])
                .plateNumber(data[1])
                .timestamp(Long.valueOf(data[2]))
                .build();

        outputService.writeOutput(exitEventService.processEvent(event));
    }

    /**
     * The app can read multiple input files.
     * And as per the specs of the input file,
     * the first line would always be the number
     * of the available lots per vehicle.
     *
     * Hence this method will do a cleanup of Redis,
     * as once the new file is processed, it will be
     * a different setup. I need to do a force cleanup
     * instead of relying to Redis' TTL capability.
     *
     * In real life scenario though, this is not advisable
     * and can severely affect performance.
     */
    private void cleanUp() {
        log.info("Cleaning up storage");
        lotService.cleanLots();
        enterEventService.cleanEvent();
        exitEventService.cleanEvent();
    }

}
