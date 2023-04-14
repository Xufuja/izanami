package dev.xfj.engine.scene;

import dev.dominion.ecs.api.Dominion;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.renderer.renderer2d.Renderer2D;
import dev.xfj.engine.scene.components.SpriteRendererComponent;
import dev.xfj.engine.scene.components.TagComponent;
import dev.xfj.engine.scene.components.TransformComponent;

public class Scene {
    private final Dominion registry;

    public Scene() {
        this.registry = Dominion.create();
    }

    public Entity createEntity(String name) {
        Entity entity = new Entity(registry.createEntity(), this);
        entity.addComponent(new TransformComponent());
        entity.addComponent(new TagComponent());
        entity.getComponent(TagComponent.class).tag = name.isEmpty() ? "Entity" : name;
        return entity;
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
