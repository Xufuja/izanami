package dev.xfj.core.window;

import dev.xfj.core.event.Event;

public class EventCallBack {
    @FunctionalInterface
    public interface EventCallbackFn {
        void handle(Event event);
    }
}
