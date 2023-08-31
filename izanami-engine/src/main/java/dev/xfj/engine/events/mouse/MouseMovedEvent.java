package dev.xfj.engine.events.mouse;

import dev.xfj.engine.events.Event;

import java.util.EnumSet;

public class MouseMovedEvent extends Event {
    private static final Event.EventType eventType = EventType.MouseMoved;

    private final float x;
    private final float y;
    public MouseMovedEvent(float x, float y) {
        super(EnumSet.of(Event.EventCategory.EventCategoryMouse, Event.EventCategory.EventCategoryInput));
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
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
