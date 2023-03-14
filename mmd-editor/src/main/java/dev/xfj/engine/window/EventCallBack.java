package dev.xfj.engine.window;

import dev.xfj.engine.event.Event;

public class EventCallBack {
    @FunctionalInterface
    public interface EventCallbackFn {
        void handle(Event event);
    }
}
