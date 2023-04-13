package dev.xfj.engine.scene.components;

import org.joml.Matrix4f;

public class TransformComponent {
    public Matrix4f transform;

    public TransformComponent() {
        this(new Matrix4f().identity());
    }
    public TransformComponent(Matrix4f transform) {
        this.transform = transform;
    }
}
