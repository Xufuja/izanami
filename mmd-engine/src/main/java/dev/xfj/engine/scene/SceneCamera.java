package dev.xfj.engine.scene;

import dev.xfj.engine.renderer.Camera;
import org.joml.Matrix4f;

public class SceneCamera extends Camera {
    private ProjectionType projectionType;
    private float perspectiveFoV;
    private float perspectiveNear;
    private float perspectiveFar;
    private float orthographicSize;
    private float orthographicNear;
    private float orthographicFar;
    private float aspectRatio;

    public enum ProjectionType {
        Perspective,
        Orthographic
    }

    public SceneCamera() {
        this.projection = new Matrix4f().identity();
        this.projectionType = ProjectionType.Orthographic;
        this.perspectiveFoV = (float) Math.toRadians(45.0f);
        this.perspectiveNear = 0.01f;
        this.perspectiveFar = 1000.0f;
        this.orthographicSize = 10.0f;
        this.orthographicNear = -1.0f;
        this.orthographicFar = 1.0f;
        this.aspectRatio = 0.0f;
        recalculateProjection();
    }

    public void setOrthographic(float size, float nearClip, float farClip) {
        projectionType = ProjectionType.Orthographic;
        orthographicSize = size;
        orthographicNear = nearClip;
        orthographicFar = farClip;
        recalculateProjection();
    }

    public void setPerspective(float verticalFoV, float nearClip, float farClip) {
        projectionType = ProjectionType.Perspective;
        perspectiveFoV = verticalFoV;
        perspectiveNear = nearClip;
        perspectiveFar = farClip;
        recalculateProjection();
    }

    public void setViewportSize(int width, int height) {
        aspectRatio = (float) width / (float) height;
        recalculateProjection();
    }

    public float getPerspectiveVerticalFoV() {
        return perspectiveFoV;
    }

    public void setPerspectiveVerticalFoV(float verticalFov) {
        perspectiveFoV = verticalFov;
        recalculateProjection();
    }

    public float getPerspectiveNearClip() {
        return perspectiveNear;
    }

    public void setPerspectiveNearClip(float nearClip) {
        perspectiveNear = nearClip;
        recalculateProjection();
    }

    public float getPerspectiveFarClip() {
        return perspectiveFar;
    }

    public void setPerspectiveFarClip(float farClip) {
        perspectiveFar = farClip;
        recalculateProjection();
    }

    public float getOrthographicSize() {
        return orthographicSize;
    }

    public void setOrthographicSize(float orthographicSize) {
        this.orthographicSize = orthographicSize;
        recalculateProjection();
    }

    public float getOrthographicNearClip() {
        return orthographicNear;
    }

    public void setOrthographicNearClip(float nearClip) {
        orthographicNear = nearClip;
        recalculateProjection();
    }

    public float getOrthographicFarClip() {
        return orthographicFar;
    }

    public void setOrthographicFarClip(float farClip) {
        orthographicFar = farClip;
        recalculateProjection();
    }

    public ProjectionType getProjectionType() {
        return projectionType;
    }

    public void setProjectionType(ProjectionType type) {
        projectionType = type;
        recalculateProjection();
    }

    public float getPerspectiveNear() {
        return perspectiveNear;
    }

    public float getPerspectiveFar() {
        return perspectiveFar;
    }

    public float getOrthographicNear() {
        return orthographicNear;
    }

    public float getOrthographicFar() {
        return orthographicFar;
    }

    private void recalculateProjection() {
        if (projectionType == ProjectionType.Perspective) {
            projection = new Matrix4f().setPerspective(perspectiveFoV, aspectRatio, perspectiveNear, perspectiveFar);
        } else {
            float orthoLeft = -orthographicSize * aspectRatio * 0.5f;
            float orthoRight = orthographicSize * aspectRatio * 0.5f;
            float orthoBottom = -orthographicSize * 0.5f;
            float orthoTop = orthographicSize * 0.5f;
            projection = new Matrix4f().setOrtho(orthoLeft, orthoRight, orthoBottom, orthoTop, orthographicNear, orthographicFar);
        }
    }
}
