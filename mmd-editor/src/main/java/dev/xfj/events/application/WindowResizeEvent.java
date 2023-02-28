package dev.xfj.events.application;

import dev.xfj.events.Event;

import java.util.EnumSet;

public class WindowResizeEvent extends Event {
    private final int width;
    private final int height;
    private final EnumSet<EventCategory> partOfCategory;

    public WindowResizeEvent(int width, int height) {
        this.width = width;
        this.height = height;
        partOfCategory = EnumSet.of(EventCategory.EventCategoryApplication);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String toString() {
        return String.format("WindowResizeEvent: %d, %d", width, height);
    }

    public static EventType getStaticType() {
        return EventType.WindowResize;
    }

    @Override
    public EventType getEventType() {
        return getStaticType();
    }

    @Override
    public String getName() {
        return EventType.WindowResize.name();
    }

    @Override
    public EnumSet<EventCategory> getCategoryFlags() {
        return partOfCategory;
    }
}