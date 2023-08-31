package dev.xfj.engine.scene;

import dev.dominion.ecs.engine.IntEntity;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.UUID;
import dev.xfj.engine.scene.components.Component;
import dev.xfj.engine.scene.components.IDComponent;
import dev.xfj.engine.scene.components.TagComponent;

public class Entity {
    private final dev.dominion.ecs.api.Entity entityHandle;
    private final Scene scene;

    public Entity(dev.dominion.ecs.api.Entity entityHandle, Scene scene) {
        this.entityHandle = entityHandle;
        this.scene = scene;
    }

    public void addComponent(Object component) {
        entityHandle.add(component);
        scene.onComponentAdded(this, (Component) entityHandle.get(component.getClass()));
    }

    public void addOrReplaceComponent(Object component) {
        Class<?> componentType = component.getClass();

        if (hasComponent(componentType)) {
            removeComponent(componentType);
        }

        addComponent(component);
    }

    public <T> T getComponent(Class<T> componentType) {
        //Should be some sort of exception
        if (!hasComponent(componentType)) {
            Log.error("Entity does not have component!");
        }
        return entityHandle.get(componentType);
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

    //The entityId is not available from the interface so perhaps that it is not supposed to be accessed?
    public int getId() {
        return ((IntEntity) entityHandle).getId();
    }

    public UUID getUUID() {
        return getComponent(IDComponent.class).id;
    }

    public String getName() {
        return getComponent(TagComponent.class).tag;
    }

    public boolean equals(Entity other) {
        return entityHandle.equals(other.entityHandle) && scene.equals(other.scene);
    }

    public dev.dominion.ecs.api.Entity getEntity() {
        return entityHandle;
    }
}
