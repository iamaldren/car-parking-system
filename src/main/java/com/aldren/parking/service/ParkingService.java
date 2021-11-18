package com.aldren.parking.service;

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
    }

    private void parseDataToEvent(String data) {

    }

}
