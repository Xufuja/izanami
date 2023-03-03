package dev.xfj.window;

import dev.xfj.events.Event;

import java.lang.reflect.InvocationTargetException;

public class EventCallBack {
    @FunctionalInterface
    public interface EventCallbackFn {
        void handle(Event event);
    }
}
