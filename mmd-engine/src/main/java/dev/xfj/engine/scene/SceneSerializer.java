package dev.xfj.engine.scene;


import dev.xfj.engine.core.Log;
import dev.xfj.engine.scene.components.CameraComponent;
import dev.xfj.engine.scene.components.SpriteRendererComponent;
import dev.xfj.engine.scene.components.TagComponent;
import dev.xfj.engine.scene.components.TransformComponent;
import dev.xfj.protobuf.*;
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

    public void serialize(Path filePath) {
        SceneFile.Builder sceneBuilder = SceneFile.newBuilder();
        sceneBuilder.setName("Untitled");

        for (Entity entity : scene.getAllEntities()) {
            dev.xfj.protobuf.EntityFile.Builder entityBuilder = dev.xfj.protobuf.EntityFile.newBuilder();

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

            if (entity.hasComponent(SpriteRendererComponent.class)) {
                Vector4f color = entity.getComponent(SpriteRendererComponent.class).color;
                entityBuilder.setSpriteRenderer(SpriteRendererFile.newBuilder()
                        .addAllColor(Arrays.asList(color.x, color.y, color.z, color.w))).build();
            }
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


    public void serializeRuntime(Path filePath) {

    }

    public boolean deserialize(Path filePath) {
        SceneFile.Builder sceneBuilder = SceneFile.newBuilder();

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            byte[] bytes = inputStream.readAllBytes();
            sceneBuilder.mergeFrom(bytes);
        } catch (IOException e) {
            Log.error("Could not open file: " + filePath);
            throw new RuntimeException(e);
        }

        SceneFile sceneFile = sceneBuilder.build();

        Log.debug(String.format("Deserializing scene: %1$s", sceneFile.getName()));

        if (!sceneFile.getEntitiesList().isEmpty()) {
            String name = "";
            for (EntityFile entity : sceneFile.getEntitiesList()) {
                if (entity.hasTag()) {
                    name = entity.getTag().getTag();
                }

                Log.debug(String.format("Deserialized entity with name = %1$s", name));
                Entity deserializedEntity = scene.createEntity(name);

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

                if (entity.hasSpriteRenderer()) {
                    SpriteRendererFile spriteRendererFile = entity.getSpriteRenderer();
                    deserializedEntity.addComponent(new SpriteRendererComponent());
                    SpriteRendererComponent spriteRendererComponent = deserializedEntity.getComponent(SpriteRendererComponent.class);

                    spriteRendererComponent.color = new Vector4f(spriteRendererFile.getColor(0), spriteRendererFile.getColor(1), spriteRendererFile.getColor(2), spriteRendererFile.getColor(2));
                }
            }
        }
        return true;
    }

    public boolean deserializeRuntime(Path filePath) {
        return true;

    }
}
