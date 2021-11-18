package com.aldren.event.service.impl;

import com.aldren.event.entity.EnterEvent;
import com.aldren.event.repository.EnterEventRepository;
import com.aldren.event.service.EventService;
import com.aldren.lot.service.LotService;
import com.aldren.properties.VehicleProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@EnableConfigurationProperties(VehicleProperties.class)
public class EnterEventService implements EventService {

    private final EnterEventRepository enterEventRepository;
    private final VehicleProperties vehicleProperties;
    private final LotService lotService;

    private static final String OUTPUT_REJECT = "Reject";
    private static final String OUTPUT_ACCEPT = "Accept %s";

    public EnterEventService(EnterEventRepository enterEventRepository,
                             VehicleProperties vehicleProperties,
                             LotService lotService) {
        this.enterEventRepository = enterEventRepository;
        this.vehicleProperties = vehicleProperties;
        this.lotService = lotService;
    }

    @Override
    public String processEvent(String[] event) {
        String nextAvailableLot = lotService.getNextAvailableLot(event[1]);
        if(!StringUtils.hasLength(nextAvailableLot)) {
            return OUTPUT_REJECT;
        }

        EnterEvent enterEvent = EnterEvent.builder()
                .plateNumber(event[2])
                .fee(vehicleProperties.getFee().get(event[1]))
                .timestamp(Long.valueOf(event[3]))
                .lot(nextAvailableLot)
                .vehicle(event[1])
                .build();

        enterEventRepository.save(enterEvent);
        lotService.parkOnNextAvailableLot(event[1], nextAvailableLot);

        return String.format(OUTPUT_ACCEPT, nextAvailableLot);
    }

}
