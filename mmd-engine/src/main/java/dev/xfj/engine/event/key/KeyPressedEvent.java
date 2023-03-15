package dev.xfj.engine.event.key;

import dev.xfj.engine.event.Event;

public class KeyPressedEvent extends KeyEvent {
    private static final EventType eventType = EventType.KeyPressed;
    private final int repeatCount;

    public KeyPressedEvent(int keyCode, int repeatCount) {
        super(keyCode);
        this.repeatCount = repeatCount;

    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public String toString() {
        return String.format("KeyPressedEvent: %1$d (%2$d repeats)", getKeyCode(), repeatCount);
    }

    public static Event.EventType getStaticType() {
        return eventType;
    }

    @Override
    public Event.EventType getEventType() {
        return getStaticType();
    }
}
