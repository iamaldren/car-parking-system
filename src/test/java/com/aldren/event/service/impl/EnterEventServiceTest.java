package com.aldren.event.service.impl;

import com.aldren.event.repository.EnterEventRepository;
import com.aldren.event.service.EventService;
import com.aldren.lot.service.LotService;
import com.aldren.properties.VehicleProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = VehicleProperties.class)
@ActiveProfiles("test")
public class EnterEventServiceTest {

    @Mock
    private LotService lotService;

    @Mock
    private EnterEventRepository enterEventRepository;

    @Autowired
    private VehicleProperties vehicleProperties;

    private EventService eventService;

    private static final String CAR_LOT_1 = "CarLot1";
    private static final String MOTORCYCLE_LOT_1 = "MotorcycleLot1";

    @BeforeEach
    public void init() {
        eventService = new EnterEventService(enterEventRepository, vehicleProperties, lotService);
    }

    @Test
    public void carOutputAcceptTest() {
        String[] carEvent = {"Enter", "car", "SGX1234A", "1613541902"};

        String carOutput = testOutputAcceptData(carEvent, CAR_LOT_1);
        String carExpectedOutput = "Accept " + CAR_LOT_1;

        assertEquals(carExpectedOutput, carOutput);
    }

    @Test
    public void motorcycleOutputAcceptTest() {
        String[] motorcycleEvent = {"Enter", "motorcycle", "SGX1234A", "1613541902"};

        String carOutput = testOutputAcceptData(motorcycleEvent, MOTORCYCLE_LOT_1);
        String carExpectedOutput = "Accept " + MOTORCYCLE_LOT_1;

        assertEquals(carExpectedOutput, carOutput);
    }

    @Test
    public void outputRejectTest() {
        String[] event = {"Enter", "motorcycle", "SGX1234A", "1613541902"};

        when(lotService.getNextAvailableLot(anyString())).thenReturn("");

        String carOutput = eventService.processEvent(event);
        String carExpectedOutput = "Reject";

        assertEquals(carExpectedOutput, carOutput);
    }

    private String testOutputAcceptData(String[] event, String lot) {
        when(lotService.getNextAvailableLot(anyString())).thenReturn(lot);
        return eventService.processEvent(event);
    }

}
