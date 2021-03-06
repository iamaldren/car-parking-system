package com.aldren.event.service.impl;

import com.aldren.event.entity.EnterEvent;
import com.aldren.event.entity.ExitEvent;
import com.aldren.event.model.Event;
import com.aldren.event.repository.EnterEventRepository;
import com.aldren.event.repository.ExitEventRepository;
import com.aldren.event.service.EventService;
import com.aldren.lot.service.LotService;
import com.aldren.util.ErrorUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
public class ExitEventService implements EventService {

    private final EnterEventRepository enterEventRepository;
    private final ExitEventRepository exitEventRepository;
    private final LotService lotService;

    private static final String OUTPUT = "%s %.0f";

    public ExitEventService(EnterEventRepository enterEventRepository,
                            ExitEventRepository exitEventRepository,
                            LotService lotService) {
        this.enterEventRepository = enterEventRepository;
        this.exitEventRepository = exitEventRepository;
        this.lotService = lotService;
    }

    @Override
    public String processEvent(Event event) {
        Optional<EnterEvent> enterEvent = enterEventRepository.findById(event.getPlateNumber());
        if(exitEventRepository.existsById(event.getPlateNumber())
                || !enterEvent.isPresent()) {
            return String.format("%1$s Vehicle with plate number of %2$s is not parked or not in the system.", ErrorUtil.ERROR_BAD_DATA, event.getPlateNumber());
        }

        if(event.getTimestamp() < enterEvent.get().getTimestamp()) {
            return String.format("%1$s Exit event time is earlier than Enter event time for plate number %2$s.", ErrorUtil.ERROR_BAD_DATA, event.getPlateNumber());
        }

        ExitEvent exitEvent = ExitEvent.builder()
                .plateNumber(event.getPlateNumber())
                .timestamp(event.getTimestamp())
                .vehicle(enterEvent.get().getVehicle())
                .lot(enterEvent.get().getLot())
                .totalFee(computeTotalFee(event.getTimestamp(), enterEvent.get().getTimestamp(), enterEvent.get().getFee()))
                .build();

        exitEventRepository.save(exitEvent);
        lotService.releasedOccupiedLot(exitEvent.getVehicle(), exitEvent.getLot());

        return String.format(OUTPUT, exitEvent.getLot(), exitEvent.getTotalFee());
    }

    @Override
    public void cleanEvent() {
        exitEventRepository.deleteAll();
    }

    private BigDecimal computeTotalFee(long exitTime, long enterTime, BigDecimal startingFee) {
        Date exitDateTime = new Date(exitTime*1000);
        Date enterDateTime = new Date(enterTime*1000);

        long differenceInMilliSeconds = Math.abs(exitDateTime.getTime() - enterDateTime.getTime());

        // Calculating the difference in Hours
        long differenceInHours = (differenceInMilliSeconds / (60 * 60 * 1000)) % 24;

        // Calculating the difference in Minutes
        long differenceInMinutes = (differenceInMilliSeconds / (60 * 1000)) % 60;

        if(differenceInHours == 0 && differenceInMinutes == 0) {
            return new BigDecimal(0);
        }

        /**
         * If minutes is greater than 0, we treat it
         * as numberOfHours + 1.
         *
         * Hence if total hours is 1, and minutes is 1.
         * The total fee will be computed as fee*2hours.
         */
        if(differenceInMinutes > 0) {
            differenceInHours = differenceInHours + 1;
        }

        return startingFee.multiply(BigDecimal.valueOf(differenceInHours));
    }

}
