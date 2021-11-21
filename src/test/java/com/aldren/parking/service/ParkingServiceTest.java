package com.aldren.parking.service;

import com.aldren.event.service.impl.EnterEventService;
import com.aldren.event.service.impl.ExitEventService;
import com.aldren.input.service.InputService;
import com.aldren.lot.service.LotService;
import com.aldren.output.OutputService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = VehicleProperties.class)
@ActiveProfiles("test")
public class ParkingServiceTest {

    @Mock
    private InputService inputService;

    @Mock
    private EnterEventService enterEventService;

    @Mock
    private ExitEventService exitEventService;

    @Mock
    private LotService lotService;

    @Mock
    private OutputService outputService;

    @Autowired
    private VehicleProperties vehicleProperties;

    private ParkingService parkingService;

    @BeforeEach
    public void init() {
        parkingService = new ParkingService(inputService, enterEventService, exitEventService, lotService, vehicleProperties, outputService);
    }

    @Test
    public void processInputHappyFlowTest() {
        List<String> file1 = new ArrayList<>();
        file1.add("3 4");
        file1.add("Enter motorcycle SGX1234A 1613541902");
        file1.add("Enter car SGF9283P 1613541902");
        file1.add("Exit SGX1234A 1613545602");
        file1.add("Enter car SGP2937F 1613546029");
        file1.add("Enter car SDW2111W 1613549730");
        file1.add("Enter car SSD9281L 1613549740");
        file1.add("Exit SDW2111W 1613559745");

        Map<String, List<String>> fileData = new HashMap<>();
        fileData.put("sample.txt", file1);

        when(inputService.processInput()).thenReturn(fileData);
        when(enterEventService.processEvent(any())).thenReturn("Test output");
        when(exitEventService.processEvent(any())).thenReturn("Test output");

        parkingService.processInput();

        verify(lotService, times(2)).setAvailableLots(anyString(), anyString(), anyInt());
        verify(enterEventService, times(5)).processEvent(any());
        verify(exitEventService, times(2)).processEvent(any());
        verify(lotService, times(1)).cleanLots();
        verify(enterEventService, times(1)).cleanEvent();
        verify(exitEventService, times(1)).cleanEvent();
        verify(outputService, times(9)).writeOutput(anyString());
    }

    @Test
    public void processInputFirstRowWrongFormatTest() {
        List<String> file = new ArrayList<>();
        file.add("3");

        Map<String, List<String>> fileData = new HashMap<>();
        fileData.put("sample.txt", file);

        when(inputService.processInput()).thenReturn(fileData);

        parkingService.processInput();

        verify(lotService, times(0)).setAvailableLots(anyString(), anyString(), anyInt());
        verify(enterEventService, times(0)).processEvent(any());
        verify(exitEventService, times(0)).processEvent(any());
        verify(lotService, times(0)).cleanLots();
        verify(enterEventService, times(0)).cleanEvent();
        verify(exitEventService, times(0)).cleanEvent();
        verify(outputService, times(2)).writeOutput(anyString());
    }

    @Test
    public void processInputFirstRowNotNumeric1Test() {
        List<String> file = new ArrayList<>();
        file.add("3 A");

        Map<String, List<String>> fileData = new HashMap<>();
        fileData.put("sample.txt", file);

        when(inputService.processInput()).thenReturn(fileData);

        verify(lotService, times(0)).setAvailableLots(anyString(), anyString(), anyInt());
        verify(enterEventService, times(0)).processEvent(any());
        verify(exitEventService, times(0)).processEvent(any());
        verify(lotService, times(0)).cleanLots();
        verify(enterEventService, times(0)).cleanEvent();
        verify(exitEventService, times(0)).cleanEvent();
        verify(outputService, times(0)).writeOutput(anyString());
    }

    @Test
    public void processInputFirstRowNotNumeric2Test() {
        List<String> file = new ArrayList<>();
        file.add("B 4");

        Map<String, List<String>> fileData = new HashMap<>();
        fileData.put("sample.txt", file);

        when(inputService.processInput()).thenReturn(fileData);

        verify(lotService, times(0)).setAvailableLots(anyString(), anyString(), anyInt());
        verify(enterEventService, times(0)).processEvent(any());
        verify(exitEventService, times(0)).processEvent(any());
        verify(lotService, times(0)).cleanLots();
        verify(enterEventService, times(0)).cleanEvent();
        verify(exitEventService, times(0)).cleanEvent();
        verify(outputService, times(0)).writeOutput(anyString());
    }

    @Test
    public void processInputEventWrongFormatTest() {
        List<String> file1 = new ArrayList<>();
        file1.add("3 4");
        file1.add("Enter motorcycle SGX1234A 1613541902");
        file1.add("Enter car SGF9283P 1613541902");
        file1.add("Exit SGX1234A 1613545602");
        file1.add("Enter car SGP2937F 1613546029");
        file1.add("Enter car SDW2111W 1613549730");
        file1.add("Enter car SSD9281L 1613549740");
        file1.add("Exit SSD9281L");
        file1.add("Exit SDW2111W 1613559745");

        Map<String, List<String>> fileData = new HashMap<>();
        fileData.put("sample.txt", file1);

        when(inputService.processInput()).thenReturn(fileData);
        when(enterEventService.processEvent(any())).thenReturn("Test output");
        when(exitEventService.processEvent(any())).thenReturn("Test output");

        parkingService.processInput();

        verify(lotService, times(2)).setAvailableLots(anyString(), anyString(), anyInt());
        verify(enterEventService, times(5)).processEvent(any());
        verify(exitEventService, times(2)).processEvent(any());
        verify(lotService, times(1)).cleanLots();
        verify(enterEventService, times(1)).cleanEvent();
        verify(exitEventService, times(1)).cleanEvent();
        verify(outputService, times(10)).writeOutput(anyString());
    }

    @Test
    public void processInputBadEnterEventTest() {
        List<String> file1 = new ArrayList<>();
        file1.add("3 4");
        file1.add("Entery motorcycle SGX1234A 1613541902");
        file1.add("Enter car SGF9283P 1613541902");
        file1.add("Exit SGX1234A 1613545602");
        file1.add("Exit car SGP2937F 1613546029");
        file1.add("Enter car SDW2111W 1613549730");
        file1.add("Enter car SSD9281L 1613549740");
        file1.add("Exit SDW2111W 1613559745");

        Map<String, List<String>> fileData = new HashMap<>();
        fileData.put("sample.txt", file1);

        when(inputService.processInput()).thenReturn(fileData);
        when(enterEventService.processEvent(any())).thenReturn("Test output");
        when(exitEventService.processEvent(any())).thenReturn("Test output");

        parkingService.processInput();

        verify(lotService, times(2)).setAvailableLots(anyString(), anyString(), anyInt());
        verify(enterEventService, times(3)).processEvent(any());
        verify(exitEventService, times(2)).processEvent(any());
        verify(lotService, times(1)).cleanLots();
        verify(enterEventService, times(1)).cleanEvent();
        verify(exitEventService, times(1)).cleanEvent();
        verify(outputService, times(9)).writeOutput(anyString());
    }

    @Test
    public void processInputBadExitEventTest() {
        List<String> file1 = new ArrayList<>();
        file1.add("3 4");
        file1.add("Enter motorcycle SGX1234A 1613541902");
        file1.add("Enter car SGF9283P 1613541902");
        file1.add("Exited SGX1234A 1613545602");
        file1.add("Enter car SGP2937F 1613546029");
        file1.add("Enter car SDW2111W 1613549730");
        file1.add("Enter car SSD9281L 1613549740");
        file1.add("Exit SDW2111W 1613559745");
        file1.add("Exes SSD9281L 1613559745");

        Map<String, List<String>> fileData = new HashMap<>();
        fileData.put("sample.txt", file1);

        when(inputService.processInput()).thenReturn(fileData);
        when(enterEventService.processEvent(any())).thenReturn("Test output");
        when(exitEventService.processEvent(any())).thenReturn("Test output");

        parkingService.processInput();

        verify(lotService, times(2)).setAvailableLots(anyString(), anyString(), anyInt());
        verify(enterEventService, times(5)).processEvent(any());
        verify(exitEventService, times(1)).processEvent(any());
        verify(lotService, times(1)).cleanLots();
        verify(enterEventService, times(1)).cleanEvent();
        verify(exitEventService, times(1)).cleanEvent();
        verify(outputService, times(10)).writeOutput(anyString());
    }

}
