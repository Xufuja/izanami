package dev.xfj.engine.scene;


import dev.xfj.engine.scene.components.CameraComponent;
import dev.xfj.engine.scene.components.SpriteRendererComponent;
import dev.xfj.engine.scene.components.TagComponent;
import dev.xfj.engine.scene.components.TransformComponent;
import dev.xfj.protobuf.SceneFile;
import dev.xfj.protobuf.TagFile;
import dev.xfj.protobuf.TransformFile;

import java.util.Arrays;

public class SceneSerializer {
    private final dev.xfj.engine.scene.Scene scene;

    public SceneSerializer(dev.xfj.engine.scene.Scene scene) {
        this.scene = scene;
    }

    public void serialize(String filepath) {
        SceneFile.Builder sceneBuilder = SceneFile.newBuilder();
        sceneBuilder.setName("Untitled");
        dev.xfj.protobuf.EntityFile.Builder entityBuilder = dev.xfj.protobuf.EntityFile.newBuilder();

        for (Entity entity : scene.getAllEntities()) {
            if (entity.hasComponent(TagComponent.class)) {
                entityBuilder.setTag(TagFile.newBuilder().setTag(entity.getComponent(TagComponent.class).tag).build());
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


            }
            if (entity.hasComponent(SpriteRendererComponent.class)) {

            }
        }
    }


    public void serializeRuntime(String filepath) {

    }

    public void deserialize(String filepath) {

    }

    public void deserializeRuntime(String filepath) {

    }
}
