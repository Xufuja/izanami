package dev.xfj.engine.event.mouse;

import dev.xfj.engine.event.Event;

public class MouseButtonPressedEvent extends MouseButtonEvent {
    private static final Event.EventType eventType = EventType.MouseButtonPressed;

    public MouseButtonPressedEvent(int button) {
        super(button);

    }
    public String toString() {
        return String.format("MouseButtonPressedEvent: %1$d", getButton());
    }

    public static Event.EventType getStaticType() {
        return eventType;
    }

    @Override
    public Event.EventType getEventType() {
        return getStaticType();
    }
}
