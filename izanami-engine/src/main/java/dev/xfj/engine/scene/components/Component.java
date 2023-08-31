package dev.xfj.engine.scene.components;

import java.util.Arrays;
import java.util.List;

public interface Component {
    List<Class<?>> allComponents = Arrays.asList(TransformComponent.class, SpriteRendererComponent.class,
            CircleRendererComponent.class, CameraComponent.class, ScriptComponent.class, NativeScriptComponent.class,
            Rigidbody2DComponent.class, BoxCollider2DComponent.class, CircleCollider2DComponent.class);

}
