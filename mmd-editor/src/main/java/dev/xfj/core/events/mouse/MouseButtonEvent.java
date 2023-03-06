package dev.xfj.core.events.mouse;

import dev.xfj.core.events.Event;

import java.util.EnumSet;

public abstract class MouseButtonEvent extends Event {
    private final int button;

    public MouseButtonEvent(int button) {
        super(EnumSet.of(Event.EventCategory.EventCategoryMouse, Event.EventCategory.EventCategoryInput));
        this.button = button;
    }

    public int getButton() {
        return button;
    }
}
