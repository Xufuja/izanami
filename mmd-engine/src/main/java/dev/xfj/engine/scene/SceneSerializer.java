package dev.xfj.engine.scene;


import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.UUID;
import dev.xfj.engine.renderer.Texture2D;
import dev.xfj.engine.scene.components.*;
import dev.xfj.protobuf.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class SceneSerializer {
    private final dev.xfj.engine.scene.Scene scene;

    public SceneSerializer(dev.xfj.engine.scene.Scene scene) {
        this.scene = scene;
    }

    public void serializeEntity(dev.xfj.protobuf.EntityFile.Builder entityBuilder, Entity entity) {
        entityBuilder.setEntity(entity.getUUID().getUUID());

        if (entity.hasComponent(TagComponent.class)) {
            entityBuilder.setTag(TagFile.newBuilder()
                    .setTag(entity.getComponent(TagComponent.class).tag).build());
        }

        if (entity.hasComponent(TransformComponent.class)) {
            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            entityBuilder.setTransform(TransformFile.newBuilder()
                    .addAllTranslation(Arrays.asList(transformComponent.translation.x, transformComponent.translation.y, transformComponent.translation.z))
                    .addAllRotation(Arrays.asList(transformComponent.rotation.x, transformComponent.rotation.y, transformComponent.rotation.z))
                    .addAllScale(Arrays.asList(transformComponent.scale.x, transformComponent.scale.y, transformComponent.scale.z))
                    .build());
        }

        if (entity.hasComponent(CameraComponent.class)) {
            CameraComponent cameraComponent = entity.getComponent(CameraComponent.class);
            SceneCamera camera = cameraComponent.camera;
            entityBuilder.setCamera(CameraFile.newBuilder()
                            .setProjectionType(camera.getProjectionType() == SceneCamera.ProjectionType.Perspective ? CameraFile.ProjectionType.Perspective : CameraFile.ProjectionType.Orthographic)
                            .setPerspectiveFov(camera.getPerspectiveVerticalFoV())
                            .setPerspectiveNear(camera.getPerspectiveNear())
                            .setPerspectiveFar(camera.getPerspectiveFar())
                            .setOrthographicSize(camera.getOrthographicSize())
                            .setOrthographicNear(camera.getOrthographicNear())
                            .setOrthographicFar(camera.getOrthographicFar())
                            .setPrimary(cameraComponent.primary)
                            .setFixedAspectRatio(cameraComponent.fixedAspectRatio))
                    .build();
        }

        if (entity.hasComponent(ScriptComponent.class)) {
            entityBuilder.setScript(ScriptFile.newBuilder()
                            .setClassName(entity.getComponent(ScriptComponent.class).className))
                    .build();
        }

        if (entity.hasComponent(SpriteRendererComponent.class)) {
            SpriteRendererComponent spriteRendererComponent = entity.getComponent(SpriteRendererComponent.class);
            entityBuilder.setSpriteRenderer(SpriteRendererFile.newBuilder()
                    .addAllColor(Arrays.asList(spriteRendererComponent.color.x, spriteRendererComponent.color.y, spriteRendererComponent.color.z, spriteRendererComponent.color.w))
                    .setTexturePath(spriteRendererComponent.texture != null ? spriteRendererComponent.texture.getPath() : "")
                    .setTilingFactor(spriteRendererComponent.tilingFactor)).build();
        }

        if (entity.hasComponent(CircleRendererComponent.class)) {
            CircleRendererComponent circleRendererComponent = entity.getComponent(CircleRendererComponent.class);
            entityBuilder.setCircleRenderer(CircleRendererFile.newBuilder()
                    .addAllColor(Arrays.asList(circleRendererComponent.color.x, circleRendererComponent.color.y, circleRendererComponent.color.z, circleRendererComponent.color.w))
                    .setThickness(circleRendererComponent.thickness)
                    .setFade(circleRendererComponent.fade)).build();
        }

        if (entity.hasComponent(Rigidbody2DComponent.class)) {
            Rigidbody2DComponent rigidbody2DComponent = entity.getComponent(Rigidbody2DComponent.class);

            entityBuilder.setRigidbody2D(Rigidbody2DFile.newBuilder()
                    .setBodyType(rigidBody2DBodyTypeToEnum(rigidbody2DComponent.type))
                    .setFixedRotation(rigidbody2DComponent.fixedRotation));
        }

        if (entity.hasComponent(BoxCollider2DComponent.class)) {
            BoxCollider2DComponent boxCollider2DComponent = entity.getComponent(BoxCollider2DComponent.class);

            entityBuilder.setBoxCollider2D(BoxCollider2DFile.newBuilder()
                            .addAllOffset(Arrays.asList(boxCollider2DComponent.offset.x, boxCollider2DComponent.offset.y))
                            .addAllSize(Arrays.asList(boxCollider2DComponent.size.x, boxCollider2DComponent.size.y))
                            .setDensity(boxCollider2DComponent.density)
                            .setFriction(boxCollider2DComponent.friction)
                            .setRestitution(boxCollider2DComponent.restitution)
                            .setRestitutionThreshold(boxCollider2DComponent.restitutionThreshold))
                    .build();
        }

        if (entity.hasComponent(CircleCollider2DComponent.class)) {
            CircleCollider2DComponent circleCollider2DComponent = entity.getComponent(CircleCollider2DComponent.class);

            entityBuilder.setCircleCollider2D(CircleCollider2DFile.newBuilder()
                            .addAllOffset(Arrays.asList(circleCollider2DComponent.offset.x, circleCollider2DComponent.offset.y))
                            .setRadius(circleCollider2DComponent.radius)
                            .setDensity(circleCollider2DComponent.density)
                            .setFriction(circleCollider2DComponent.friction)
                            .setRestitution(circleCollider2DComponent.restitution)
                            .setRestitutionThreshold(circleCollider2DComponent.restitutionThreshold))
                    .build();
        }
    }

    public void serialize(Path filePath) {
        SceneFile.Builder sceneBuilder = SceneFile.newBuilder();
        sceneBuilder.setName("Untitled");

        for (Entity entity : scene.getAllEntities()) {
            dev.xfj.protobuf.EntityFile.Builder entityBuilder = dev.xfj.protobuf.EntityFile.newBuilder();

            serializeEntity(entityBuilder, entity);

            sceneBuilder.addEntities(entityBuilder.build());
        }

        byte[] bytes = sceneBuilder.build().toByteArray();
        try (OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE)) {
            outputStream.write(bytes);
        } catch (IOException e) {
            Log.error("Could not open file: " + filePath);
            throw new RuntimeException(e);
        }
    }

    private Rigidbody2DFile.BodyType rigidBody2DBodyTypeToEnum(Rigidbody2DComponent.BodyType type) {
        return switch (type) {
            case Static -> Rigidbody2DFile.BodyType.Static;
            case Dynamic -> Rigidbody2DFile.BodyType.Dynamic;
            case Kinematic -> Rigidbody2DFile.BodyType.Kinematic;
        };
    }


    public void serializeRuntime(Path filePath) {

    }

    public boolean deserialize(Path filePath) {
        SceneFile.Builder sceneBuilder = SceneFile.newBuilder();

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            byte[] bytes = inputStream.readAllBytes();
            sceneBuilder.mergeFrom(bytes);
        } catch (IOException e) {
            Log.error(String.format("Failed to load .scene file '%1$s'\n     {%2$s}", filePath, e.getMessage()));
            throw new RuntimeException(e);
        }

        SceneFile sceneFile = sceneBuilder.build();

        Log.debug(String.format("Deserializing scene: %1$s", sceneFile.getName()));

        if (!sceneFile.getEntitiesList().isEmpty()) {
            String name = "";
            for (EntityFile entity : sceneFile.getEntitiesList()) {
                long uuid = entity.getEntity();

                if (entity.hasTag()) {
                    name = entity.getTag().getTag();
                }

                Log.debug(String.format("Deserialized entity with ID = %1$d, name = %2$s", uuid, name));
                Entity deserializedEntity = scene.createEntityWithUUID(new UUID(uuid), name);

                if (entity.hasTransform()) {
                    TransformFile transformFile = entity.getTransform();
                    TransformComponent transformComponent = deserializedEntity.getComponent(TransformComponent.class);

                    transformComponent.translation = new Vector3f(transformFile.getTranslation(0), transformFile.getTranslation(1), transformFile.getTranslation(2));
                    transformComponent.rotation = new Vector3f(transformFile.getRotation(0), transformFile.getRotation(1), transformFile.getRotation(2));
                    transformComponent.scale = new Vector3f(transformFile.getScale(0), transformFile.getScale(1), transformFile.getScale(2));
                }

                if (entity.hasCamera()) {
                    CameraFile cameraFile = entity.getCamera();
                    deserializedEntity.addComponent(new CameraComponent());
                    CameraComponent cameraComponent = deserializedEntity.getComponent(CameraComponent.class);

                    cameraComponent.camera.setProjectionType(cameraFile.getProjectionType() == CameraFile.ProjectionType.Perspective ? SceneCamera.ProjectionType.Perspective : SceneCamera.ProjectionType.Orthographic);
                    cameraComponent.camera.setPerspectiveVerticalFoV(cameraFile.getPerspectiveFov());
                    cameraComponent.camera.setPerspectiveNearClip(cameraFile.getPerspectiveNear());
                    cameraComponent.camera.setPerspectiveFarClip(cameraFile.getPerspectiveFar());

                    cameraComponent.camera.setOrthographicSize(cameraFile.getOrthographicSize());
                    cameraComponent.camera.setOrthographicNearClip(cameraFile.getOrthographicNear());
                    cameraComponent.camera.setOrthographicFarClip(cameraFile.getOrthographicFar());

                    cameraComponent.primary = cameraFile.getPrimary();
                    cameraComponent.fixedAspectRatio = cameraFile.getFixedAspectRatio();
                }

                if (entity.hasScript()) {
                    ScriptFile scriptFile = entity.getScript();
                    deserializedEntity.addComponent(new ScriptComponent(scriptFile.getClassName()));
                }

                if (entity.hasSpriteRenderer()) {
                    SpriteRendererFile spriteRendererFile = entity.getSpriteRenderer();
                    String texturePath = spriteRendererFile.getTexturePath();
                    if (!texturePath.isEmpty() && !texturePath.isBlank()) {
                        deserializedEntity.addComponent(new SpriteRendererComponent(new Vector4f(spriteRendererFile.getColor(0), spriteRendererFile.getColor(1), spriteRendererFile.getColor(2), spriteRendererFile.getColor(3)), Texture2D.create(Path.of(spriteRendererFile.getTexturePath())), spriteRendererFile.getTilingFactor()));
                    } else {
                        deserializedEntity.addComponent(new SpriteRendererComponent(new Vector4f(spriteRendererFile.getColor(0), spriteRendererFile.getColor(1), spriteRendererFile.getColor(2), spriteRendererFile.getColor(3)), spriteRendererFile.getTilingFactor()));
                    }
                }

                if (entity.hasCircleRenderer()) {
                    CircleRendererFile circleRendererFile = entity.getCircleRenderer();
                    deserializedEntity.addComponent(new CircleRendererComponent(new Vector4f(circleRendererFile.getColor(0), circleRendererFile.getColor(1), circleRendererFile.getColor(2), circleRendererFile.getColor(3)), circleRendererFile.getThickness(), circleRendererFile.getFade()));
                }

                if (entity.hasRigidbody2D()) {
                    Rigidbody2DFile rigidbody2DFile = entity.getRigidbody2D();
                    deserializedEntity.addComponent(new Rigidbody2DComponent(rigidBody2DBodyTypeFromEnum(rigidbody2DFile.getBodyType()), rigidbody2DFile.getFixedRotation()));
                }

                if (entity.hasBoxCollider2D()) {
                    BoxCollider2DFile boxCollider2DFile = entity.getBoxCollider2D();
                    deserializedEntity.addComponent(new BoxCollider2DComponent(new Vector2f(boxCollider2DFile.getOffset(0), boxCollider2DFile.getOffset(1)), new Vector2f(boxCollider2DFile.getSize(0), boxCollider2DFile.getSize(1)), boxCollider2DFile.getDensity(), boxCollider2DFile.getFriction(), boxCollider2DFile.getRestitution(), boxCollider2DFile.getRestitutionThreshold()));
                }

                if (entity.hasCircleCollider2D()) {
                    CircleCollider2DFile circleCollider2DFile = entity.getCircleCollider2D();
                    deserializedEntity.addComponent(new CircleCollider2DComponent(new Vector2f(circleCollider2DFile.getOffset(0), circleCollider2DFile.getOffset(1)), circleCollider2DFile.getRadius(), circleCollider2DFile.getDensity(), circleCollider2DFile.getFriction(), circleCollider2DFile.getRestitution(), circleCollider2DFile.getRestitutionThreshold()));
                }
            }
        }
        return true;
    }

    private Rigidbody2DComponent.BodyType rigidBody2DBodyTypeFromEnum(Rigidbody2DFile.BodyType type) {
        return switch (type) {
            case Static -> Rigidbody2DComponent.BodyType.Static;
            case Dynamic -> Rigidbody2DComponent.BodyType.Dynamic;
            case Kinematic -> Rigidbody2DComponent.BodyType.Kinematic;
            //Some sort of exception
            default -> {
                Log.error("Unknown body type");
                yield Rigidbody2DComponent.BodyType.Static;
            }
        };
    }

    public boolean deserializeRuntime(Path filePath) {
        return true;

    }
}
