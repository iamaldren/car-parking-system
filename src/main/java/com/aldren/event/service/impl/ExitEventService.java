package com.aldren.event.service.impl;

import com.aldren.event.model.Event;
import com.aldren.event.service.EventService;
import org.springframework.stereotype.Service;

@Service
public class ExitEventService implements EventService {

    @Override
    public String processEvent(Event event) {
        return "";
    }

}
