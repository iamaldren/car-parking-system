package com.aldren.event.service.impl;

import com.aldren.event.entity.EnterEvent;
import com.aldren.event.model.Event;
import com.aldren.event.repository.EnterEventRepository;
import com.aldren.event.service.EventService;
import com.aldren.lot.service.LotService;
import com.aldren.properties.VehicleProperties;
import com.aldren.util.ErrorUtil;
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
    public String processEvent(Event event) {
        if(enterEventRepository.existsById(event.getPlateNumber())) {
            return String.format(ErrorUtil.ERROR_BAD_DATA, "Vehicle with given plate number is already parked");
        }

        String nextAvailableLot = lotService.getNextAvailableLot(event.getVehicleType());
        if(!StringUtils.hasLength(nextAvailableLot)) {
            return OUTPUT_REJECT;
        }

        EnterEvent enterEvent = EnterEvent.builder()
                .plateNumber(event.getPlateNumber())
                .fee(vehicleProperties.getFee().get(event.getVehicleType()))
                .timestamp(event.getTimestamp())
                .lot(nextAvailableLot)
                .vehicle(event.getVehicleType())
                .build();

        enterEventRepository.save(enterEvent);
        lotService.parkOnNextAvailableLot(event.getVehicleType(), nextAvailableLot);

        return String.format(OUTPUT_ACCEPT, nextAvailableLot);
    }

    @Override
    public void cleanEvent() {
        enterEventRepository.deleteAll();
    }

}
