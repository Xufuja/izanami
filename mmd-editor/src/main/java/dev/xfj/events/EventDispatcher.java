package dev.xfj.events;

public class EventDispatcher {
    private Event.EventType eventType;

    public EventDispatcher(Event.EventType eventType) {
        this.eventType = eventType;
    }

    public boolean dispatch(){
        //TODO
        return false;
    }
}
