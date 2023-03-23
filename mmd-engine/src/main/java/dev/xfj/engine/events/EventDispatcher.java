package dev.xfj.engine.events;

import dev.xfj.engine.core.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class EventDispatcher {
    private final Event event;

    public EventDispatcher(Event event) {
        this.event = event;
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> boolean dispatch(Class<T> eventType, Function<T, Boolean> func) {
        try {
            Method getStaticEventType = eventType.getDeclaredMethod("getStaticType");
            if (event.getEventType() == getStaticEventType.invoke(null)) {
                event.handled = func.apply((T) event);
                return true;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.error(e.getMessage());
        }
        return false;
    }
}