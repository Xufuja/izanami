package dev.xfj.engine.scene;

import dev.dominion.ecs.api.Dominion;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.renderer.Camera;
import dev.xfj.engine.renderer.renderer2d.Renderer2D;
import dev.xfj.engine.scene.components.*;
import org.joml.Matrix4f;

public class Scene {
    private final Dominion registry;
    private int viewportWidth;
    private int viewportHeight;

    public Scene() {
        this.registry = Dominion.create();
        this.viewportWidth = 0;
        this.viewportHeight = 0;
    }

    public Entity createEntity(String name) {
        Entity entity = new Entity(registry.createEntity(), this);
        entity.addComponent(new TransformComponent());
        entity.addComponent(new TagComponent());
        entity.getComponent(TagComponent.class).tag = name.isEmpty() ? "Entity" : name;
        return entity;
    }

    @SuppressWarnings("unchecked")
    public void onUpdate(TimeStep ts) {
        registry.findEntitiesWith(NativeScriptComponent.class)
                .stream().forEach(entity -> {
                    NativeScriptComponent<ScriptableEntity> nsc = entity.comp();
                    if (nsc.instance == null) {
                        nsc.instance = nsc.instantiateScript.get();
                        nsc.instance.entity = new Entity(entity.entity(), this);
                        nsc.instance.onCreate();
                    }
                    nsc.instance.onUpdate(ts);
                });

        Camera mainCamera = null;
        Matrix4f cameraTransform = null;

        for (var it = registry.findEntitiesWith(TransformComponent.class, CameraComponent.class).iterator(); it.hasNext(); ) {
            var entity = it.next();
            TransformComponent transform = entity.comp1();
            CameraComponent camera = entity.comp2();
            if (camera.primary) {
                mainCamera = camera.camera;
                cameraTransform = transform.getTransform();
                break;
            }
        }
        if (mainCamera != null) {
            Renderer2D.beginScene(mainCamera, cameraTransform);

            registry.findEntitiesWith(TransformComponent.class, SpriteRendererComponent.class)
                    .stream().forEach(entity -> {
                        TransformComponent transform = entity.comp1();
                        SpriteRendererComponent sprite = entity.comp2();
                        Renderer2D.drawQuad(transform.getTransform(), sprite.color);
                    });

            Renderer2D.endScene();
        }
    }

    public void onViewportResize(int width, int height) {
        viewportWidth = width;
        viewportHeight = height;

        registry.findEntitiesWith(CameraComponent.class)
                .stream().forEach(entity -> {
                    CameraComponent cameraComponent = entity.comp();
                    if (!cameraComponent.fixedAspectRatio) {
                        cameraComponent.camera.setViewportSize(width, height);
                    }
                });
    }

    public Dominion getRegistry() {
        return registry;
    }
}
