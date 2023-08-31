package dev.xfj.engine.scene;

import dev.xfj.engine.core.TimeStep;

public abstract class ScriptableEntity {
    public Entity entity;

    public <T> T getComponent(Class<T> componentType) {
        return entity.getComponent(componentType);
    }

    protected abstract void onCreate();
    protected abstract void onDestroy();
    protected abstract void onUpdate(TimeStep ts);
}
