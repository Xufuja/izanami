package dev.xfj.engine.renderer;

import org.joml.Matrix4f;

public class Camera {
    private final Matrix4f projection;

    public Camera() {
        this(new Matrix4f().identity());
    }

    public Camera(Matrix4f projection) {
        this.projection = projection;
    }

    public Matrix4f getProjection() {
        return projection;
    }
}
