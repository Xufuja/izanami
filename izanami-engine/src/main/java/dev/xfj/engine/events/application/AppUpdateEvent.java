package dev.xfj.engine.events.application;

import dev.xfj.engine.events.Event;

import java.util.EnumSet;

public class AppUpdateEvent extends Event {
    private static final EventType eventType = EventType.AppUpdate;

    public AppUpdateEvent() {
        super(EnumSet.of(Event.EventCategory.EventCategoryApplication));
    }

    public static Event.EventType getStaticType() {
        return eventType;
    }

    @Override
    public Event.EventType getEventType() {
        return getStaticType();
    }


}