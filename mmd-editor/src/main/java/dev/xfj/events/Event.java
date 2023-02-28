package dev.xfj.events;

import java.util.EnumSet;
import java.util.Set;

public abstract class Event {
    private boolean handled;

    protected Event() {
        handled = false;
    }

    public enum EventType {
        None,
        WindowClose, WindowResize, WindowFocus, WindowLostFocus, WindowMoved,
        AppTick, AppUpdate, AppRender,
        KeyPressed, KeyReleased,
        MouseButtonPressed, MouseButtonReleased, MouseMoved, MouseScrolled
    }

    public enum EventCategory {
        None(0),
        EventCategoryApplication(1 << 0),
        EventCategoryInput(1 << 1),
        EventCategoryKeyboard(1 << 2),
        EventCategoryMouse(1 << 3),
        EventCategoryMouseButton(1 << 4);

        private final long categoryValue;

        EventCategory(long categoryValue) {
            this.categoryValue = categoryValue;
        }

        public long getCategoryValue() {
            return categoryValue;
        }
    }

    public EnumSet<Event.EventCategory> getEventCategory(long statusValue) {
        EnumSet<Event.EventCategory> categoryFlags = EnumSet.noneOf(Event.EventCategory.class);
        for (Event.EventCategory statusFlag : Event.EventCategory.values()) {
            long flagValue = statusFlag.getCategoryValue();
            if ((flagValue & statusValue) == flagValue) {
                categoryFlags.add(statusFlag);
            }
        }
        return categoryFlags;
    }

    public long getFlagValue(Set<Event.EventCategory> flags) {
        long value = 0;
        for (Event.EventCategory statusFlag : flags) {
            value |= statusFlag.getCategoryValue();
        }
        return value;
    }

    public boolean isInCategory(EventCategory eventCategory) {
        return getCategoryFlags().contains(eventCategory);
    }

    public static EventType getStaticEventType() {
        return EventType.None;
    }

    public abstract EventType getEventType();

    public abstract String getName();

    public abstract EnumSet<EventCategory> getCategoryFlags();

}