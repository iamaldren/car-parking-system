package com.aldren.lot.service;

import com.aldren.exception.VehicleNotSupportedException;
import com.aldren.lot.entity.LotAvailability;
import com.aldren.lot.repository.LotAvailabilityRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

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
     * E.g Available lots are Lot4, Lot5. Then a vehicle parked on
     * Lot2 exited. The available lots will now be Lot4, Lot5, and
     * Lot2. When the next vehicle enters, it will be assigned to
     * Lot2.
     */
    public String getNextAvailableLot(String vehicleType) throws VehicleNotSupportedException {
        Optional<LotAvailability> lotAvailability = lotAvailabilityRepository.findById(vehicleType);
        if(!lotAvailability.isPresent()) {
            throw new VehicleNotSupportedException(String.format("Vehicle %s is not yet supported.", vehicleType));
        }

        Optional<Map.Entry<String, String>> availableLots = lotAvailability.get()
                .getAvailableLots()
                .entrySet()
                .stream()
                .filter(map -> !map.getValue().equals(OCCUPIED_LOT))
                .findFirst();

        if(availableLots.isPresent()) {
            return availableLots.get().getKey();
        }

        return Strings.EMPTY;
    }

    public void parkOnNextAvailableLot(String vehicleType, String lot) {
        updateLot(vehicleType, lot, OCCUPIED_LOT);
    }

    public void releasedOccupiedLot(String vehicleType, String lot) {
        updateLot(vehicleType, lot, AVAILABLE_LOT);
    }

    public void cleanLots() {
        lotAvailabilityRepository.deleteAll();
    }

    private void updateLot(String vehicleType, String lot, String status) {
        LotAvailability availability = lotAvailabilityRepository.findById(vehicleType).get();
        availability.getAvailableLots().put(lot, status);
        lotAvailabilityRepository.save(availability);
    }

}
