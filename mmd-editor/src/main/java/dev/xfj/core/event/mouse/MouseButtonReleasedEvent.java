package dev.xfj.core.event.mouse;

import dev.xfj.core.event.Event;

public class MouseButtonReleasedEvent extends MouseButtonEvent {
    private static final Event.EventType eventType = EventType.MouseButtonReleased;

    public MouseButtonReleasedEvent(int button) {
        super(button);

    }
    public String toString() {
        return String.format("MouseButtonReleasedEvent: %1$d", getButton());
    }

    public static Event.EventType getStaticType() {
        return eventType;
    }

    @Override
    public Event.EventType getEventType() {
        return getStaticType();
    }
}
