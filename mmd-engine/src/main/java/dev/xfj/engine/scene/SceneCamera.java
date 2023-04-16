package dev.xfj.engine.scene;

import dev.xfj.engine.renderer.Camera;
import org.joml.Matrix4f;

public class SceneCamera implements Camera {
    private Matrix4f projection;
    private float orthographicSize;
    private float orthographicNear;
    private float orthographicFar;
    private float aspectRatio;

    public SceneCamera() {
        this.projection = new Matrix4f().identity();
        this.orthographicSize = 10.0f;
        this.orthographicNear = -1.0f;
        this.orthographicFar = 1.0f;
        this.aspectRatio = 0.0f;
        recalculateProjection();
    }

    public void setOrthographic(float size, float nearClip, float farClip) {
        orthographicSize = size;
        orthographicNear = nearClip;
        orthographicFar = farClip;
        recalculateProjection();
    }

    public void setViewportSize(int width, int height) {
        aspectRatio = (float) width / (float) height;
        recalculateProjection();
    }

    public float getOrthographicSize() {
        return orthographicSize;
    }

    public void setOrthographicSize(float orthographicSize) {
        this.orthographicSize = orthographicSize;
        recalculateProjection();
    }

    private void recalculateProjection() {
        float orthoLeft = -orthographicSize * aspectRatio * 0.5f;
        float orthoRight = orthographicSize * aspectRatio * 0.5f;
        float orthoBottom = -orthographicSize * 0.5f;
        float orthoTop = orthographicSize * 0.5f;
        projection = new Matrix4f().setOrtho(orthoLeft, orthoRight, orthoBottom, orthoTop, orthographicNear, orthographicFar);
    }

    @Override
    public Matrix4f getProjection() {
        return projection;
    }
}
