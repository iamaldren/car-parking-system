package com.aldren.parking.service;

import com.aldren.event.model.Event;
import com.aldren.event.service.impl.EnterEventService;
import com.aldren.event.service.impl.ExitEventService;
import com.aldren.input.service.InputService;
import com.aldren.lot.service.LotService;
import com.aldren.util.ErrorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ParkingService {

    private InputService inputService;
    private EnterEventService enterEventService;
    private ExitEventService exitEventService;
    private LotService lotService;

    private static final String VEHICLE_CAR = "car";
    private static final String VEHICLE_MOTORCYCLE = "motorcycle";

    private static final String LOT_NAME_CAR = "CarLot";
    private static final String LOT_NAME_MOTORCYCLE = "MotorcycleLot";

    private static final String EVENT_ENTER = "Enter";
    private static final String EVENT_EXIT = "Exit";

    public ParkingService(InputService inputService,
                          EnterEventService enterEventService,
                          ExitEventService exitEventService,
                          LotService lotService) {
        this.inputService = inputService;
        this.enterEventService = enterEventService;
        this.exitEventService = exitEventService;
        this.lotService = lotService;
    }

    public void processInput() {
        inputService.processInput()
                .stream()
                .filter(data -> isAvailableLotsSet(data.get(0)))
                .forEach(this::processEachFile);
    }

    private boolean isAvailableLotsSet(String lotCount) {
        String[] availableLots = lotCount.trim().split("//s+");

        if(availableLots.length != 2) {
            System.out.println(String.format(ErrorUtil.ERROR_BAD_DATA, "Skipping file, wrong format for lot count."));
            return false;
        }

        if(!isDataNumeric(availableLots[0])
                || !isDataNumeric(availableLots[1])) {
            System.out.println(String.format(ErrorUtil.ERROR_BAD_DATA, "Skipping file, lot count is not numeric"));
            return false;
        }

        lotService.setAvailableLots(VEHICLE_CAR, LOT_NAME_CAR, Integer.parseInt(availableLots[0]));
        lotService.setAvailableLots(VEHICLE_MOTORCYCLE, LOT_NAME_MOTORCYCLE, Integer.parseInt(availableLots[1]));

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
        String[] splitData = data.trim().split("//s+");

        switch(splitData.length) {
            case 4:
                processEnterEvent(splitData);
                break;
            case 3:
                processExitEvent(splitData);
                break;
            default:
                System.out.println(String.format(ErrorUtil.ERROR_BAD_DATA, "Skipping event, wrong format."));
        }
    }

    private void processEnterEvent(String[] data) {
        if(!EVENT_ENTER.equals(data[0])) {
            System.out.println(String.format(ErrorUtil.ERROR_BAD_DATA, "Bad event, not recognized."));
            return;
        }

        Event event = Event.builder()
                .event(data[0])
                .vehicleType(data[1])
                .plateNumber(data[2])
                .timestamp(Long.valueOf(data[3]))
                .build();

        System.out.println(enterEventService.processEvent(event));
    }

    private void processExitEvent(String[] data) {
        if(!EVENT_EXIT.equals(data[0])) {
            System.out.println(String.format(ErrorUtil.ERROR_BAD_DATA, "Bad event, not recognized."));
            return;
        }

        Event event = Event.builder()
                .event(data[0])
                .plateNumber(data[2])
                .timestamp(Long.valueOf(data[3]))
                .build();

        System.out.println(exitEventService.processEvent(event));
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
     * and can severly affect performance.
     */
    private void cleanUp() {
        lotService.cleanLots();
        enterEventService.cleanEvent();
        exitEventService.cleanEvent();
    }

}
