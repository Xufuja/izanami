package dev.xfj.engine.scene;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Results;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.renderer.Camera;
import dev.xfj.engine.renderer.renderer2d.Renderer2D;
import dev.xfj.engine.scene.components.CameraComponent;
import dev.xfj.engine.scene.components.SpriteRendererComponent;
import dev.xfj.engine.scene.components.TagComponent;
import dev.xfj.engine.scene.components.TransformComponent;
import org.joml.Matrix4f;

import java.util.Iterator;

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
        Camera mainCamera = null;
        Matrix4f cameraTransform = null;

        for (var it = registry.findEntitiesWith(TransformComponent.class, CameraComponent.class).iterator(); it.hasNext(); ) {
            var entity = it.next();
            TransformComponent transform = entity.comp1();
            CameraComponent camera = entity.comp2();
            if (camera.primary) {
                mainCamera = camera.camera;
                cameraTransform = transform.transform;
                break;
            }
        }
        if (mainCamera != null) {
            Renderer2D.beginScene(mainCamera, cameraTransform);

            registry.findEntitiesWith(TransformComponent.class, SpriteRendererComponent.class)
                    .stream().forEach(entity -> {
                        TransformComponent transform = entity.comp1();
                        SpriteRendererComponent sprite = entity.comp2();
                        Renderer2D.drawQuad(transform.transform, sprite.color);
                    });

            Renderer2D.endScene();
        }
    }
}
