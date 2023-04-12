package dev.xfj.engine.scene.components;

import org.joml.Matrix4f;

public class TransformComponent {
    private Matrix4f transform;

    public TransformComponent() {
        this(new Matrix4f().identity());
    }
    public TransformComponent(Matrix4f transform) {
        this.transform = transform;
    }

    public Matrix4f getTransform() {
        return transform;
    }

    public void setTransform(Matrix4f transform) {
        this.transform = transform;
    }
}
