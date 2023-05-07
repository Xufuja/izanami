package dev.xfj.engine.renderer;

import dev.xfj.engine.core.Input;
import dev.xfj.engine.core.KeyCodes;
import dev.xfj.engine.core.MouseButtonCodes;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.events.EventDispatcher;
import dev.xfj.engine.events.mouse.MouseScrolledEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class EditorCamera extends Camera {
    private float fov;
    private float aspectRatio;
    private float nearClip;
    private float farClip;
    private Matrix4f viewMatrix;
    private Matrix4f projection;
    private Vector3f position;
    private Vector3f focalPoint;
    private Vector2f initialMousePosition;
    private float distance;
    private float pitch;
    private float yaw;
    private float viewportWidth;
    private float viewportHeight;

    public EditorCamera() {
        this(45.0f, 1.778f, 0.01f, 1000.0f);
    }

    public EditorCamera(float fov, float aspectRatio, float nearClip, float farClip) {
        this.viewMatrix = new Matrix4f().identity();
        this.projection = new Matrix4f().perspective((float) Math.toRadians(fov), aspectRatio, nearClip, farClip);
        this.position = new Vector3f(0.0f, 0.0f, 0.0f);
        this.focalPoint = new Vector3f(0.0f, 0.0f, 0.0f);
        this.initialMousePosition = new Vector2f(0.0f, 0.0f);
        this.distance = 10.0f;
        this.pitch = 0.0f;
        this.yaw = 0.0f;
        this.viewportWidth = 1280;
        this.viewportHeight = 720;

        updateView();
    }

    public void onUpdate(TimeStep ts) {
        if (Input.isKeyPressed(KeyCodes.LEFT_ALT)) {
            Vector2f mouse = new Vector2f(Input.getMouseX(), Input.getMouseY());
            Vector2f delta = new Vector2f(mouse.sub(initialMousePosition, new Vector2f()).mul(0.003f));
            initialMousePosition = mouse;

            if (Input.isMouseButtonPressed(MouseButtonCodes.BUTTON_MIDDLE)) {
                mousePan(delta);
            } else if (Input.isMouseButtonPressed(MouseButtonCodes.BUTTON_LEFT)) {
                mouseRotate(delta);
            } else if (Input.isMouseButtonPressed(MouseButtonCodes.BUTTON_RIGHT)) {
                mouseZoom(delta.y);
            }
        }
        updateView();
    }

    public void onEvent(Event event) {
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        eventDispatcher.dispatch(MouseScrolledEvent.class, this::onMouseScroll);
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setViewportSize(float width, float height) {
        viewportWidth = width;
        viewportHeight = height;
        updateProjection();
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getViewProjection() {
        return projection.mul(viewMatrix, new Matrix4f());
    }

    public Vector3f getUpDirection() {
        return getOrientation().rotateYXZ(0, 0, 0).transform(new Vector3f(0, 1, 0)).normalize();
    }

    public Vector3f getRightDirection() {
        return getOrientation().rotateYXZ(0, 0, 0).transform(new Vector3f(1, 0, 0)).normalize();
    }

    public Vector3f getForwardDirection() {
        return getOrientation().rotateYXZ(0, 0, 0).transform(new Vector3f(0, 0, -1)).normalize();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternionf getOrientation() {
        Quaternionf orientation = new Quaternionf();
        orientation.rotateX(-pitch);
        orientation.rotateY(-yaw);
        return orientation;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    private void updateProjection() {
        aspectRatio = viewportWidth / viewportHeight;
        projection = new Matrix4f().perspective((float) Math.toRadians(fov), aspectRatio, nearClip, farClip);
    }

    private void updateView() {
        position = calculatePosition();
        Quaternionf orientation = getOrientation();
        viewMatrix = new Matrix4f().translate(position).mul(orientation.get(new Matrix4f()), new Matrix4f()).invert();
    }

    private boolean onMouseScroll(MouseScrolledEvent event) {
        float delta = event.getyOffset() * 0.1f;
        mouseZoom(delta);
        updateView();

        return false;
    }

    private void mousePan(Vector2f delta) {
        Vector2f speed = panSpeed();
        focalPoint.add(getRightDirection().mul(-1.0f, new Vector3f()).mul(delta.x).mul(speed.x).mul(distance));
        focalPoint.add(getUpDirection().mul(delta.y, new Vector3f()).mul(speed.y).mul(distance));
    }

    private void mouseRotate(Vector2f delta) {
        float yawSign = getUpDirection().y < 0 ? -1.0f : 1.0f;
        yaw += yawSign * delta.x * rotationSpeed();
        pitch += delta.y * rotationSpeed();
    }

    private void mouseZoom(float delta) {
        distance -= delta * zoomSpeed();
        if (distance < 1.0f) {
            focalPoint.add(getForwardDirection());
            distance = 1.0f;
        }
    }

    private Vector3f calculatePosition() {
        return focalPoint.sub(getForwardDirection(), new Vector3f()).mul(distance);
    }

    private Vector2f panSpeed() {
        float x = Math.min(viewportWidth / 1000.0f, 2.4f);
        float xFactor = 0.0366f * (x * x) - 0.1778f * x + 0.3021f;

        float y = Math.min(viewportHeight / 1000.0f, 2.4f);
        float yFactor = 0.0366f * (y * y) - 0.1778f * y + 0.3021f;

        return new Vector2f(xFactor, yFactor);
    }

    private float rotationSpeed() {
        return 0.8f;
    }

    private float zoomSpeed() {
        float distance = this.distance * 0.2f;
        distance = Math.max(distance, 0.0f);
        float speed = distance * distance;
        speed = Math.min(speed, 100.0f); // max speed = 100
        return speed;
    }

}
