package dev.xfj.core.event.mouse;

import dev.xfj.core.event.Event;

import java.util.EnumSet;

public class MouseScrolledEvent extends Event {
    private static final Event.EventType eventType = EventType.MouseScrolled;

    private final float xOffset;
    private final float yOffset;
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
