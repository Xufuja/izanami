package dev.xfj.engine.scene;

import dev.xfj.engine.core.Log;

public class Entity {
    private final dev.dominion.ecs.api.Entity entityHandle;
    private Scene scene;

    public Entity(dev.dominion.ecs.api.Entity entityHandle, Scene scene) {
        this.entityHandle = entityHandle;
        this.scene = scene;
    }

    public void addComponent(Object a) {
        entityHandle.add(a);
    }
    public <T> T getComponent(Class<T> componentType) {
        //Should be some sort of exception
        if (!hasComponent(componentType)) {
            Log.error("Entity does not have component!");
        }
        return (T) entityHandle.get(componentType);
    }
    public boolean hasComponent(Class<?> componentType) {
        return entityHandle.has(componentType);
    }
    public void removeComponent(Class<?> componentType) {
        //Should be some sort of exception
        if (!hasComponent(componentType)) {
            Log.error("Entity does not have component!");
        }
        entityHandle.remove(getComponent(componentType));
    }
}
