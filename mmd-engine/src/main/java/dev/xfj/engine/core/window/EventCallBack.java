package dev.xfj.engine.core.window;

import dev.xfj.engine.event.Event;

public class EventCallBack {
    @FunctionalInterface
    public interface EventCallbackFn {
        void handle(Event event);
    }
}
