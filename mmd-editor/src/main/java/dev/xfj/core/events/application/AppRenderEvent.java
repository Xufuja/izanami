package dev.xfj.core.events.application;

import dev.xfj.core.events.Event;

import java.util.EnumSet;

public class AppRenderEvent extends Event {
    private static final EventType eventType = EventType.AppRender;

    public AppRenderEvent() {
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