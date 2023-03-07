package dev.xfj.core.events.mouse;

import dev.xfj.core.events.Event;

import java.util.EnumSet;

public class MouseScrolledEvent extends Event {
    private static final Event.EventType eventType = EventType.MouseScrolled;

    private float xOffset;
    private float yOffset;
    public MouseScrolledEvent(float xOffset, float yOffset) {
        super(EnumSet.of(Event.EventCategory.EventCategoryMouse, Event.EventCategory.EventCategoryInput));
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public String toString() {
        return String.format("MouseScrolledEvent: %1$f, %2$f", xOffset, yOffset);
    }

    public static Event.EventType getStaticType() {
        return eventType;
    }

    @Override
    public Event.EventType getEventType() {
        return getStaticType();
    }
}
