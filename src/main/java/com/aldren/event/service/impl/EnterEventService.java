package com.aldren.event.service.impl;

import com.aldren.event.entity.EnterEvent;
import com.aldren.event.repository.EnterEventRepository;
import com.aldren.event.service.EventService;
import com.aldren.properties.VehicleProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(VehicleProperties.class)
public class EnterEventService implements EventService {

    private final EnterEventRepository enterEventRepository;
    private final VehicleProperties vehicleProperties;

    public EnterEventService(EnterEventRepository enterEventRepository,
                             VehicleProperties vehicleProperties) {
        this.enterEventRepository = enterEventRepository;
        this.vehicleProperties = vehicleProperties;
    }

    @Override
    public void processEvent(String[] event) {
        /***
         * TODO
         *
         * Check available car lot.
         */

        EnterEvent enterEvent = EnterEvent.builder()
                .plateNumber(event[2])
                .fee(vehicleProperties.getFee().get(event[1]))
                .timestamp(Long.valueOf(event[3]))
                .build();

        enterEventRepository.save(enterEvent);
    }

}
