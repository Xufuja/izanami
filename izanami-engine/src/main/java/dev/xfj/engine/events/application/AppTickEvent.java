package dev.xfj.engine.events.application;

import dev.xfj.engine.events.Event;

import java.util.EnumSet;

public class AppTickEvent extends Event {
    private static final EventType eventType = EventType.AppTick;

    public AppTickEvent() {
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