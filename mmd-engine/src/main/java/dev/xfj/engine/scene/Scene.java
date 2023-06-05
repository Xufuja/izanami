package dev.xfj.engine.scene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.dominion.ecs.api.Dominion;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.core.UUID;
import dev.xfj.engine.renderer.Camera;
import dev.xfj.engine.renderer.EditorCamera;
import dev.xfj.engine.renderer.renderer2d.Renderer2D;
import dev.xfj.engine.scene.components.*;
import org.joml.Matrix4f;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Scene {
    private final Dominion registry;
    private final Map<Integer, dev.dominion.ecs.api.Entity> entityIdMapping;
    private int lastId;
    private int viewportWidth;
    private int viewportHeight;
    private World physicsWorld;

    public static BodyDef.BodyType rigidbody2DTypeToBox2DBody(Rigidbody2DComponent.BodyType bodyType) {
        return switch (bodyType) {
            case Static -> BodyDef.BodyType.StaticBody;
            case Dynamic -> BodyDef.BodyType.DynamicBody;
            case Kinematic -> BodyDef.BodyType.KinematicBody;
            case default -> {
                //Some sort of exception
                Log.error("Unknown body type");
                yield BodyDef.BodyType.StaticBody;
            }
        };
    }

    public Scene() {
        this.registry = Dominion.create();
        this.entityIdMapping = new HashMap<>();
        this.lastId = 0;
        this.viewportWidth = 0;
        this.viewportHeight = 0;
    }

    public static void copyComponent(Class<?> componentType, Dominion src, Map<UUID, dev.dominion.ecs.api.Entity> entityMap) {
        for (var it = src.findEntitiesWith(componentType).iterator(); it.hasNext(); ) {
            var entity = it.next();

            UUID uuid = entity.entity().get(IDComponent.class).id;
            //Some sort of exception HZ_CORE_ASSERT(enttMap.find(uuid) != enttMap.end());
            dev.dominion.ecs.api.Entity dstEntityId = entityMap.get(uuid);

            if (dstEntityId.has(componentType)) {
                dstEntityId.remove(dstEntityId.get(componentType));
            }
            dstEntityId.add(duplicateComponent(componentType, entity.entity().get(componentType)));
        }
    }

    public static void copyComponentIfExists(Class<?> componentType, Entity dst, Entity src) {
        if (src.hasComponent(componentType)) {
            dst.addOrReplaceComponent(duplicateComponent(componentType, src.getComponent(componentType)));
        }
    }

    private static Object duplicateComponent(Class<?> componentType, Object src) {
        try {
            Constructor<?> constructor = componentType.getConstructor(componentType);
            constructor.setAccessible(true);
            return constructor.newInstance(src);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Scene copy(Scene other) {
        Scene newScene = new Scene();

        newScene.viewportWidth = other.viewportWidth;
        newScene.viewportHeight = other.viewportHeight;

        Dominion srcSceneRegistry = other.registry;
        Map<UUID, dev.dominion.ecs.api.Entity> entityMap = new HashMap<>();

        for (var it = srcSceneRegistry.findEntitiesWith(IDComponent.class).iterator(); it.hasNext(); ) {
            var entity = it.next();
            UUID uuid = entity.entity().get(IDComponent.class).id;
            String name = entity.entity().get(TagComponent.class).tag;
            Entity newEntity = newScene.createEntityWithUUID(uuid, name);
            entityMap.put(uuid, newEntity.getEntity());
        }

        Component.allComponents.forEach(component -> copyComponent(component, srcSceneRegistry, entityMap));

        return newScene;
    }

    public Entity createEntity(String name) {
        return createEntityWithUUID(new UUID(), name);
    }

    public Entity createEntityWithUUID(UUID uuid, String name) {
        Entity entity = new Entity(registry.createEntity(), this);

        entity.addComponent(new IDComponent(uuid));
        entity.addComponent(new TransformComponent());
        entity.addComponent(new TagComponent());
        entity.getComponent(TagComponent.class).tag = name.isEmpty() ? "Entity" : name;

        entityIdMapping.put(lastId + 1, entity.getEntity());
        lastId++;

        return entity;
    }

    public void destroyEntity(dev.dominion.ecs.api.Entity entity) {
        registry.deleteEntity(entity);
    }

    public void onRuntimeStart() {
        onPhysics2DStart();
    }

    public void onRuntimeStop() {
        onPhysics2DStop();
    }

    public void onSimulationStart() {
        onPhysics2DStart();
    }

    public void onSimulationStop() {
        onPhysics2DStop();
    }

    @SuppressWarnings("unchecked")
    public void onUpdateRuntime(TimeStep ts) {
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

        int velocityIterations = 6;
        int positionIterations = 2;

        physicsWorld.step(ts.getTime(), velocityIterations, positionIterations);

        registry.findEntitiesWith(Rigidbody2DComponent.class)
                .stream().forEach(component -> {
                    Entity entity = new Entity(component.entity(), this);
                    TransformComponent transform = entity.getComponent(TransformComponent.class);
                    Rigidbody2DComponent rb2dc = entity.getComponent(Rigidbody2DComponent.class);

                    Body body = rb2dc.runtimeBody;
                    Vector2 position = body.getPosition();
                    transform.translation.x = position.x;
                    transform.translation.y = position.y;
                    transform.rotation.z = body.getAngle();
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
                        Renderer2D.drawSprite(transform.getTransform(), sprite, findEntityId(entity.entity()));
                    });

            registry.findEntitiesWith(TransformComponent.class, CircleRendererComponent.class)
                    .stream().forEach(entity -> {
                        TransformComponent transform = entity.comp1();
                        CircleRendererComponent circle = entity.comp2();
                        Renderer2D.drawCircle(transform.getTransform(), circle.color, circle.thickness, circle.fade, findEntityId(entity.entity()));
                    });

            Renderer2D.endScene();
        }
    }

    public void onUpdateSimulation(TimeStep ts, EditorCamera camera) {
        int velocityIterations = 6;
        int positionIterations = 2;

        physicsWorld.step(ts.getTime(), velocityIterations, positionIterations);

        registry.findEntitiesWith(Rigidbody2DComponent.class)
                .stream().forEach(component -> {
                    Entity entity = new Entity(component.entity(), this);
                    TransformComponent transform = entity.getComponent(TransformComponent.class);
                    Rigidbody2DComponent rb2dc = entity.getComponent(Rigidbody2DComponent.class);

                    Body body = rb2dc.runtimeBody;
                    Vector2 position = body.getPosition();
                    transform.translation.x = position.x;
                    transform.translation.y = position.y;
                    transform.rotation.z = body.getAngle();
                });

        renderScene(camera);
    }

    public void onUpdateEditor(TimeStep ts, EditorCamera camera) {
      renderScene(camera);
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

    public void duplicateEntity(Entity entity) {
        Entity newEntity = createEntity(entity.getName());
        Component.allComponents.forEach(component -> copyComponentIfExists(component, newEntity, entity));
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

    public Entity getEntityById(int id) {
        if (entityIdMapping.get(id) != null) {
            return new Entity(entityIdMapping.get(id), this);
        } else {
            return null;
        }
    }

    public int findEntityId(dev.dominion.ecs.api.Entity entity) {
        for (Map.Entry<Integer, dev.dominion.ecs.api.Entity> entry : entityIdMapping.entrySet()) {
            if (entity.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private void onPhysics2DStart() {
        physicsWorld = new World(new Vector2(0.0f, -9.8f), true);

        registry.findEntitiesWith(Rigidbody2DComponent.class)
                .stream().forEach(component -> {
                    Entity entity = new Entity(component.entity(), this);
                    TransformComponent transform = entity.getComponent(TransformComponent.class);
                    Rigidbody2DComponent rb2dc = entity.getComponent(Rigidbody2DComponent.class);

                    BodyDef bodyDef = new BodyDef();
                    bodyDef.type = rigidbody2DTypeToBox2DBody(rb2dc.type);
                    bodyDef.position.set(transform.translation.x, transform.translation.y);
                    bodyDef.angle = transform.rotation.z;

                    Body body = physicsWorld.createBody(bodyDef);
                    body.setFixedRotation(rb2dc.fixedRotation);
                    rb2dc.runtimeBody = body;

                    if (entity.hasComponent(BoxCollider2DComponent.class)) {
                        BoxCollider2DComponent bc2dc = entity.getComponent(BoxCollider2DComponent.class);

                        PolygonShape boxShape = new PolygonShape();
                        boxShape.setAsBox(bc2dc.size.x * transform.scale.x, bc2dc.size.y * transform.scale.y);

                        FixtureDef fixtureDef = new FixtureDef();
                        fixtureDef.shape = boxShape;
                        fixtureDef.density = bc2dc.density;
                        fixtureDef.friction = bc2dc.friction;
                        fixtureDef.restitution = bc2dc.restitution;
                        body.createFixture(fixtureDef);
                    }

                    if (entity.hasComponent(CircleCollider2DComponent.class)) {
                        CircleCollider2DComponent cc2dc = entity.getComponent(CircleCollider2DComponent.class);

                        CircleShape circleShape = new CircleShape();
                        circleShape.setPosition(new Vector2(cc2dc.offset.x, cc2dc.offset.y));
                        circleShape.setRadius(transform.scale.x * cc2dc.radius);

                        FixtureDef fixtureDef = new FixtureDef();
                        fixtureDef.shape = circleShape;
                        fixtureDef.density = cc2dc.density;
                        fixtureDef.friction = cc2dc.friction;
                        fixtureDef.restitution = cc2dc.restitution;
                        body.createFixture(fixtureDef);
                    }
                });
    }

    private void onPhysics2DStop() {
        physicsWorld = null;
    }

    private void renderScene(EditorCamera camera) {
        Renderer2D.beginScene(camera);

        registry.findEntitiesWith(TransformComponent.class, SpriteRendererComponent.class)
                .stream().forEach(entity -> {
                    TransformComponent transform = entity.comp1();
                    SpriteRendererComponent sprite = entity.comp2();
                    Renderer2D.drawSprite(transform.getTransform(), sprite, findEntityId(entity.entity()));
                });

        registry.findEntitiesWith(TransformComponent.class, CircleRendererComponent.class)
                .stream().forEach(entity -> {
                    TransformComponent transform = entity.comp1();
                    CircleRendererComponent circle = entity.comp2();
                    Renderer2D.drawCircle(transform.getTransform(), circle.color, circle.thickness, circle.fade, findEntityId(entity.entity()));
                });

        Renderer2D.endScene();
    }

    protected <T extends Component> void onComponentAdded(Entity entity, T component) {
        switch (component) {
            case TransformComponent tfc -> {
            }
            case CameraComponent cc ->
                    ((CameraComponent) component).camera.setViewportSize(viewportWidth, viewportHeight);
            case SpriteRendererComponent src -> {
            }
            case TagComponent tc -> {
            }
            case NativeScriptComponent<?> nsc -> {
            }
            case Rigidbody2DComponent rb2dc -> {
            }
            case BoxCollider2DComponent bc2dc -> {
            }
            case IDComponent ic -> {
            }
            case CircleRendererComponent crc -> {
            }
            case CircleCollider2DComponent cc2dc -> {
            }
            default -> Log.error("Invalid component type");
        }
    }
}
