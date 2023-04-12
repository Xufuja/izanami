package dev.xfj.engine.scene;

import dev.dominion.ecs.api.Dominion;
import dev.xfj.engine.scene.components.TransformComponent;
import org.joml.Matrix4f;

public class Scene {
    private Dominion registry;

    public Scene() {
        this.registry =  Dominion.create();
        registry.createEntity(new TransformComponent(new Matrix4f().identity()));

    }
}
