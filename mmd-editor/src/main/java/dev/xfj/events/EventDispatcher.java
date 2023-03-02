package dev.xfj.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class EventDispatcher {
    private Event event;

    public EventDispatcher(Event event) {
        this.event = event;
    }

    public <T extends Event> boolean dispatch(Function<T, Boolean> handler) {
        Class<?> eventType = event.getClass();
        while (eventType != null) {
            try {
                Method method = eventType.getDeclaredMethod("getStaticEventType");
                @SuppressWarnings("unchecked")
                Class<T> castedEventType = (Class<T>) method.invoke(null);
                if (castedEventType.isAssignableFrom(eventType)) {
                    T castedEvent = castedEventType.cast(event);
                    handler.apply(castedEvent);
                    event.setHandled(true);
                    return event.isHandled();
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException e) {
                eventType = eventType.getSuperclass();
            }
        }
        return false;
    }

}
