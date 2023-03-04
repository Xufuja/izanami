package dev.xfj.events;

import dev.xfj.Log;
import dev.xfj.window.Window;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class EventDispatcher {
    public static final Logger logger = Log.init(Window.class.getSimpleName());
    private final Event event;

    public EventDispatcher(Event event) {
        this.event = event;
    }

    public <T extends Event> boolean dispatch(Class<T> eventType, Function<T, Boolean> func) {
        try {
            Method getStaticEventType = eventType.getDeclaredMethod("getStaticType");
            if (event.getEventType() == getStaticEventType.invoke(null)) {
                event.handled = func.apply((T) event);
                return true;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}