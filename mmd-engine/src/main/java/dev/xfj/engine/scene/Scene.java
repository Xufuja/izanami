package dev.xfj.engine.scene;

import dev.dominion.ecs.api.Dominion;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.renderer.Camera;
import dev.xfj.engine.renderer.renderer2d.Renderer2D;
import dev.xfj.engine.scene.components.*;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

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

    public void destroyEntity(dev.dominion.ecs.api.Entity entity) {
        registry.deleteEntity(entity);
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

    public Entity getPrimaryCameraEntity() {
        Entity result = null;
        for (var it = registry.findEntitiesWith(CameraComponent.class).iterator(); it.hasNext(); ) {
            var entity = it.next();
            CameraComponent cameraComponent = entity.comp();
            if (cameraComponent.primary) {
                result = new Entity(entity.entity(), this);
            }

        }
        return result;
    }

    public Dominion getRegistry() {
        return registry;
    }

    public ArrayList<Entity> getAllEntities() {
        ArrayList<Entity> result = new ArrayList<>();
        for (var it = registry.findEntitiesWith(TagComponent.class).iterator(); it.hasNext(); ) {
            var entity = it.next();
            result.add(new Entity(entity.entity(), this));

        }
        return result;
    }


    protected <T extends Component> void onComponentAdded(Entity entity, T component) {
        switch (component) {
            case TransformComponent tfc -> Log.trace("TransformComponent Unimplemented");
            case CameraComponent cc ->
                    ((CameraComponent) component).camera.setViewportSize(viewportWidth, viewportHeight);
            case SpriteRendererComponent src -> Log.trace("SpriteRendererComponent Unimplemented");
            case TagComponent tc -> Log.trace("TagComponent Unimplemented");
            case NativeScriptComponent<?> nsc -> Log.trace("NativeScriptComponent<?> Unimplemented");
            default -> Log.error("Invalid component type");
        }
    }
}
