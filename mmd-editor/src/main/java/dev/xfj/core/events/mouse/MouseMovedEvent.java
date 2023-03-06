package dev.xfj.core.events.mouse;

import dev.xfj.core.events.Event;

import java.util.EnumSet;

public class MouseMovedEvent extends Event {
    private static final Event.EventType eventType = EventType.MouseMoved;

    private float x;
    private float y;
    public MouseMovedEvent(float x, float y) {
        super(EnumSet.of(Event.EventCategory.EventCategoryMouse, Event.EventCategory.EventCategoryInput));
        this.x = x;
        this.y = y;
    }
    public String toString() {
        return String.format("MouseMovedEvent: %1$f, %2$f", x, y);
    }

    public static Event.EventType getStaticType() {
        return eventType;
    }

    @Override
    public Event.EventType getEventType() {
        return getStaticType();
    }
}
