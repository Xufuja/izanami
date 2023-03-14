package dev.xfj.engine.event.key;

import dev.xfj.engine.event.Event;

public class KeyTypedEvent extends KeyEvent {
    private static final EventType eventType = EventType.KeyTyped;

    public KeyTypedEvent(int keyCode) {
        super(keyCode);

    }

    public String toString() {
        return String.format("KeyTypedEvent: %1$d", getKeyCode());
    }

    public static Event.EventType getStaticType() {
        return eventType;
    }

    @Override
    public Event.EventType getEventType() {
        return getStaticType();
    }
}
