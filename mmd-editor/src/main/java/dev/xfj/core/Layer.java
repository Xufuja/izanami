package dev.xfj.core;

import dev.xfj.core.events.Event;

public abstract class Layer {
    protected String debugName;

    public Layer() {
        this("Layer");
    }
    public Layer(String name) {
     this.debugName = name;
    }
    public abstract void onAttach();
    public abstract void onDetach();
    public abstract void onUpdate();
    public abstract void onEvent(Event event);

    public String getDebugName() {
        return debugName;
    }
}
