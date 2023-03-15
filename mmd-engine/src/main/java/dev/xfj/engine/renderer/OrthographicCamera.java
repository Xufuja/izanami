package dev.xfj.engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class OrthographicCamera {
    private final Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Matrix4f viewProjectionMatrix;
    private Vector3f position;
    private float rotation;

    public OrthographicCamera(float left, float right, float bottom, float top) {
        this.position = new Vector3f(0.0f, 0.0f, 5.0f);
        this.rotation = 0.0f;
        this.projectionMatrix = new Matrix4f().setOrtho(left, right, bottom, top, -1, 1.0f);
        this.viewMatrix = new Matrix4f().identity();
        this.viewProjectionMatrix = this.projectionMatrix.mul(this.viewMatrix);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        recalculateViewMatrix();
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        recalculateViewMatrix();
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getViewProjectionMatrix() {
        return viewProjectionMatrix;
    }

    private void recalculateViewMatrix() {
        Matrix4f transform = new Matrix4f().translate(position.x, position.y, position.z).rotate((float) Math.toRadians(rotation), 0, 0, 1);
        viewMatrix = new Matrix4f(transform).invert();
        viewProjectionMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix);
    }
}
