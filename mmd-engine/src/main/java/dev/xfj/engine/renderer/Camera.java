package dev.xfj.engine.renderer;

import org.joml.Matrix4f;

public abstract class Camera {
    protected Matrix4f projection;

    public Matrix4f getProjection() {
        return projection;
    }
}
