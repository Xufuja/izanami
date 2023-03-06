package dev.xfj.core.window;

import dev.xfj.core.events.Event;

public class EventCallBack {
    @FunctionalInterface
    public interface EventCallbackFn {
        void handle(Event event);
    }
}
