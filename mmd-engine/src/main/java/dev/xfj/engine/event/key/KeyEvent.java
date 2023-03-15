package dev.xfj.engine.event.key;

import dev.xfj.engine.event.Event;

import java.util.EnumSet;

public abstract class KeyEvent extends Event {
    private final int keyCode;

    public KeyEvent(int keyCode) {
        super(EnumSet.of(EventCategory.EventCategoryKeyboard, EventCategory.EventCategoryInput));
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }


}
