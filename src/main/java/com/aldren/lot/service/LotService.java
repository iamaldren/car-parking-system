package com.aldren.lot.service;

import com.aldren.lot.entity.LotAvailability;
import com.aldren.lot.repository.LotAvailabilityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LotService {

    private final LotAvailabilityRepository lotAvailabilityRepository;

    private static final String AVAILABLE_LOT = "1";
    private static final String OCCUPIED_LOT = "0";

    public LotService(LotAvailabilityRepository lotAvailabilityRepository) {
        this.lotAvailabilityRepository = lotAvailabilityRepository;
    }

    /**
     *
     * @param vehicleType
     * @param lotName
     * @param lotCount
     *
     * Creating lot slots.
     */
    public void setAvailableLots(String vehicleType, String lotName, int lotCount) {
        Map<String, String> availableLots = new LinkedHashMap<>();

        for(int i = 1; i <= lotCount; i++) {
            availableLots.put(lotName+i, AVAILABLE_LOT);
        }

        log.info("Available Lots for {} is {}", vehicleType, availableLots.size());

        lotAvailabilityRepository.save(LotAvailability.builder()
                .vehicleType(vehicleType)
                .availableLots(availableLots)
                .build());
    }

    /**
     *
     * @param vehicleType
     * @return
     *
     * Will always return the available lot with lowest number.
     *
     * E.g Available lots are Lot2, Lot4, Lot5. It will return Lot2
     */
    public String getNextAvailableLot(String vehicleType) {
        return lotAvailabilityRepository
                .findByVehicleType(vehicleType)
                .getAvailableLots()
                .entrySet()
                .stream()
                .filter(map -> !map.getValue().equals(OCCUPIED_LOT))
                .findFirst()
                .get()
                .getKey();
    }

    public void parkOnNextAvailableLot(String vehicleType, String lot) {
        updateLot(vehicleType, lot, OCCUPIED_LOT);
    }

    public void releasedOccupiedLot(String vehicleType, String lot) {
        updateLot(vehicleType, lot, AVAILABLE_LOT);
    }

    public void cleanLots(List<String> vehicleTypes) {
        lotAvailabilityRepository.deleteAllById(vehicleTypes);
    }

    private void updateLot(String vehicleType, String lot, String status) {
        LotAvailability availability = lotAvailabilityRepository.findByVehicleType(vehicleType);
        availability.getAvailableLots().put(lot, status);
        lotAvailabilityRepository.save(availability);
    }

}
