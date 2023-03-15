package dev.xfj.engine;

import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.event.Event;

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
    public abstract void onUpdate(TimeStep ts);
    public abstract void onImGuiRender();
    public abstract void onEvent(Event event);

    public String getDebugName() {
        return debugName;
    }
}
