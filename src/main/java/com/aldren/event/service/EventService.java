package com.aldren.event.service;

import com.aldren.event.model.Event;

public interface EventService {

    String processEvent(Event event);

    void cleanEvent();

}
