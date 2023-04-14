package dev.xfj.engine.scene;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.renderer.renderer2d.Renderer2D;
import dev.xfj.engine.scene.components.SpriteRendererComponent;
import dev.xfj.engine.scene.components.TransformComponent;
import org.joml.Matrix4f;

public class Scene {
    private final Dominion registry;

    public Scene() {
        this.registry = Dominion.create();
        registry.createEntity(new TransformComponent(new Matrix4f().identity()));
    }

    public Entity createEntity() {
        return registry.createEntity();
    }

    public Dominion getRegistry() {
        return registry;
    }

    public void onUpdate(TimeStep ts) {

        registry.findEntitiesWith(TransformComponent.class, SpriteRendererComponent.class)
                .stream().forEach(entity -> {
                    TransformComponent transform = entity.comp1();
                    SpriteRendererComponent color = entity.comp2();
                    Renderer2D.drawQuad(transform.transform, color.color);
                });

    }
}
