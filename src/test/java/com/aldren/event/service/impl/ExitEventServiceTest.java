package com.aldren.event.service.impl;

import com.aldren.event.entity.EnterEvent;
import com.aldren.event.model.Event;
import com.aldren.event.repository.EnterEventRepository;
import com.aldren.event.repository.ExitEventRepository;
import com.aldren.event.service.EventService;
import com.aldren.lot.service.LotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ExitEventServiceTest {

    @Mock
    private EnterEventRepository enterEventRepository;

    @Mock
    private ExitEventRepository exitEventRepository;

    @Mock
    private LotService lotService;

    private EventService eventService;

    private static final String PLATE_NUMBER = "SGX1234A";

    @BeforeEach
    public void init() {
        eventService = new ExitEventService(enterEventRepository, exitEventRepository, lotService);
    }

    @Test
    public void outputExitSuccessTest() {
        EnterEvent enterEvent = EnterEvent.builder()
                .vehicle("motorcycle")
                .plateNumber(PLATE_NUMBER)
                .lot("MotorcycleLot1")
                .timestamp(Long.valueOf(1613541902))
                .fee(new BigDecimal(1))
                .build();

        when(enterEventRepository.findById(anyString())).thenReturn(Optional.of(enterEvent));
        when(exitEventRepository.existsById(anyString())).thenReturn(false);

        Event event = Event.builder()
                .plateNumber(PLATE_NUMBER)
                .event("Exit")
                .timestamp(Long.valueOf(1613545602))
                .build();

        String output = eventService.processEvent(event);

        verify(exitEventRepository, times(1)).save(any());
        verify(lotService, times(1)).releasedOccupiedLot(anyString(), anyString());

        String expectedOutput = "MotorcycleLot1 2";

        assertEquals(expectedOutput, output);
    }

    @Test
    public void outputExitPlateNumberExistsInExitEventTest() {
        EnterEvent enterEvent = EnterEvent.builder()
                .vehicle("motorcycle")
                .plateNumber(PLATE_NUMBER)
                .lot("MotorcycleLot1")
                .timestamp(Long.valueOf(1613541902))
                .fee(new BigDecimal(1))
                .build();

        when(enterEventRepository.findById(anyString())).thenReturn(Optional.of(enterEvent));
        when(exitEventRepository.existsById(anyString())).thenReturn(true);

        Event event = Event.builder()
                .plateNumber(PLATE_NUMBER)
                .event("Exit")
                .timestamp(Long.valueOf(1613545602))
                .build();

        String output = eventService.processEvent(event);

        String expectedOutput = "Bad Data:: Vehicle with plate number of SGX1234A is not parked or not in the system.";

        assertEquals(expectedOutput, output);
    }

    @Test
    public void outputExitPlateNumberDoesntExistsInEnterEventTest() {
        when(enterEventRepository.findById(anyString())).thenReturn(Optional.empty());

        Event event = Event.builder()
                .plateNumber(PLATE_NUMBER)
                .event("Exit")
                .timestamp(Long.valueOf(1613545602))
                .build();

        String output = eventService.processEvent(event);

        String expectedOutput = "Bad Data:: Vehicle with plate number of SGX1234A is not parked or not in the system.";

        assertEquals(expectedOutput, output);
    }

    @Test
    public void cleanLotsTest() {
        eventService.cleanEvent();

        verify(exitEventRepository, times(1)).deleteAll();
    }

    @Test
    public void outputExitEventTimeEarlierThanEnterEventTimeTest() {
        EnterEvent enterEvent = EnterEvent.builder()
                .vehicle("motorcycle")
                .plateNumber(PLATE_NUMBER)
                .lot("MotorcycleLot1")
                .timestamp(Long.valueOf(1613545602))
                .fee(new BigDecimal(1))
                .build();

        when(enterEventRepository.findById(anyString())).thenReturn(Optional.of(enterEvent));
        when(exitEventRepository.existsById(anyString())).thenReturn(false);

        Event event = Event.builder()
                .plateNumber(PLATE_NUMBER)
                .event("Exit")
                .timestamp(Long.valueOf(1613541902))
                .build();

        String output = eventService.processEvent(event);

        String expectedOutput = "Bad Data:: Exit event time is earlier than Enter event time for plate number SGX1234A.";

        assertEquals(expectedOutput, output);
    }

    @Test
    public void outputTotalFeeIs0Test() {
        EnterEvent enterEvent = EnterEvent.builder()
                .vehicle("motorcycle")
                .plateNumber(PLATE_NUMBER)
                .lot("MotorcycleLot1")
                .timestamp(Long.valueOf(1613541902))
                .fee(new BigDecimal(1))
                .build();

        when(enterEventRepository.findById(anyString())).thenReturn(Optional.of(enterEvent));
        when(exitEventRepository.existsById(anyString())).thenReturn(false);

        Event event = Event.builder()
                .plateNumber(PLATE_NUMBER)
                .event("Exit")
                .timestamp(Long.valueOf(1613541902))
                .build();

        String output = eventService.processEvent(event);

        verify(exitEventRepository, times(1)).save(any());
        verify(lotService, times(1)).releasedOccupiedLot(anyString(), anyString());

        String expectedOutput = "MotorcycleLot1 0";

        assertEquals(expectedOutput, output);
    }

}
