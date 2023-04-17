package dev.xfj.engine.scene;

import dev.xfj.engine.core.TimeStep;

public abstract class ScriptableEntity {
    public Entity entity;

    public <T> T getComponent(Class<T> componentType) {
        return entity.getComponent(componentType);
    }

    public abstract void onCreate();
    public abstract void onDestroy();
    public abstract void onUpdate(TimeStep ts);
}
