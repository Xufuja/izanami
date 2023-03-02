package dev.xfj.window;

import dev.xfj.events.Event;

public class EventCallBack {
    @FunctionalInterface
    public interface EventCallbackFn {
        void handleEvent(Event event);
    }
}
