package com.aldren.lot.service;

import com.aldren.exception.VehicleNotSupportedException;
import com.aldren.lot.entity.LotAvailability;
import com.aldren.lot.repository.LotAvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LotServiceTest {

    @Spy
    private LotAvailabilityRepository lotAvailabilityRepository;

    private LotService lotService;

    @BeforeEach
    public void init() {
        lotService = new LotService(lotAvailabilityRepository);
    }

    @Test
    public void setAvailableLotsTest() {
        int lotCount = 4;

        lotService.setAvailableLots("car", "CarLot", lotCount);

        verify(lotAvailabilityRepository, times(1)).save(any());
    }

    @Test
    public void getNextAvailableLotTest() throws VehicleNotSupportedException {
        mockAvailableLotsData();

        String availableLot = lotService.getNextAvailableLot("car");
        assertEquals("CarLot2", availableLot);
    }

    @Test
    public void parkOnNextAvailableLotTest() throws VehicleNotSupportedException {
        mockAvailableLotsData();

        lotService.parkOnNextAvailableLot("car", "CarLot2");

        verify(lotAvailabilityRepository, times(1)).findById(any());
        verify(lotAvailabilityRepository, times(1)).save(any());

        String nextAvailableLot = lotService.getNextAvailableLot("car");
        assertEquals("CarLot4", nextAvailableLot);
    }

    @Test
    public void releasedOccupiedLotTest() throws VehicleNotSupportedException {
        mockAvailableLotsData();

        lotService.releasedOccupiedLot("car", "CarLot1");

        verify(lotAvailabilityRepository, times(1)).findById(any());
        verify(lotAvailabilityRepository, times(1)).save(any());

        String nextAvailableLot = lotService.getNextAvailableLot("car");
        assertEquals("CarLot1", nextAvailableLot);
    }

    @Test
    public void cleanLotsTest() {
        lotService.cleanLots();

        verify(lotAvailabilityRepository, times(1)).deleteAll();
    }

    private void mockAvailableLotsData() {
        Map<String, String> lots = new LinkedHashMap<>();
        lots.put("CarLot1", "0");
        lots.put("CarLot2", "1");
        lots.put("CarLot3", "0");
        lots.put("CarLot4", "1");
        lots.put("CarLot5", "1");

        LotAvailability availability = LotAvailability.builder()
                .vehicleType("car")
                .availableLots(lots)
                .build();

        doReturn(Optional.of(availability)).when(lotAvailabilityRepository).findById(anyString());
    }

}
