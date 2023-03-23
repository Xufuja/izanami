package dev.xfj.engine.renderer;

import dev.xfj.engine.core.Input;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.events.EventDispatcher;
import dev.xfj.engine.events.application.WindowResizeEvent;
import dev.xfj.engine.events.mouse.MouseScrolledEvent;
import org.joml.Vector3f;

import static dev.xfj.engine.core.KeyCodes.*;

public class OrthographicCameraController {
    private float aspectRatio;
    private float zoomLevel;
    private final OrthographicCamera camera;
    private final boolean rotation;
    private final Vector3f cameraPosition;
    private float cameraRotation;
    private float cameraTranslationSpeed;
    private final float cameraRotationSpeed;

    public OrthographicCameraController(float aspectRatio) {
        this(aspectRatio, false);
    }

    public OrthographicCameraController(float aspectRatio, boolean rotation) {
        this.aspectRatio = aspectRatio;
        this.rotation = rotation;
        this.zoomLevel = 1.0f;
        this.camera = new OrthographicCamera(-this.aspectRatio * this.zoomLevel, this.aspectRatio * this.zoomLevel, -this.zoomLevel, this.zoomLevel);
        this.cameraPosition = new Vector3f(0.0f, 0.0f, 0.0f);
        this.cameraRotation = 0.0f;
        this.cameraTranslationSpeed = 5.0f;
        this.cameraRotationSpeed = 180.0f;
    }

    public void onUpdate(TimeStep ts) {
        if (Input.isKeyPressed(MMD_KEY_A)) {
            cameraPosition.x -= Math.cos(Math.toRadians(cameraRotation)) * cameraTranslationSpeed * ts.getTime();
            cameraPosition.y -= Math.sin(Math.toRadians(cameraRotation)) * cameraTranslationSpeed * ts.getTime();
        } else if (Input.isKeyPressed(MMD_KEY_D)) {
            cameraPosition.x += Math.cos(Math.toRadians(cameraRotation)) * cameraTranslationSpeed * ts.getTime();
            cameraPosition.y += Math.sin(Math.toRadians(cameraRotation)) * cameraTranslationSpeed * ts.getTime();
        }
        if (Input.isKeyPressed(MMD_KEY_W)) {
            cameraPosition.x += Math.sin(Math.toRadians(cameraRotation)) * cameraTranslationSpeed * ts.getTime();
            cameraPosition.y -= Math.cos(Math.toRadians(cameraRotation)) * cameraTranslationSpeed * ts.getTime();

        } else if (Input.isKeyPressed(MMD_KEY_S)) {
            cameraPosition.x -= Math.sin(Math.toRadians(cameraRotation)) * cameraTranslationSpeed * ts.getTime();
            cameraPosition.y += Math.cos(Math.toRadians(cameraRotation)) * cameraTranslationSpeed * ts.getTime();
        }

        if (this.rotation) {
            if (Input.isKeyPressed(MMD_KEY_Q)) {
                cameraRotation += cameraRotationSpeed * ts.getTime();
            }
            if (Input.isKeyPressed(MMD_KEY_E)) {
                cameraRotation -= cameraRotationSpeed * ts.getTime();
            }
            if (cameraRotation > 180.0f) {
                cameraRotation -= 360.0f;
            } else if (cameraRotation <= -180.0f) {
                cameraRotation += 360.0f;
            }
            camera.setRotation(cameraRotation);
        }
        camera.setPosition(cameraPosition);
        cameraTranslationSpeed = zoomLevel;

    }

    public void onEvent(Event event) {
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        eventDispatcher.dispatch(MouseScrolledEvent.class, this::onMouseScrolled);
        eventDispatcher.dispatch(WindowResizeEvent.class, this::onWindowResized);
    }

    public OrthographicCamera getCamera() {
        return this.camera;
    }

    public float getZoomLevel() {
        return this.zoomLevel;
    }

    public void setZoomLevel(float zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    private boolean onMouseScrolled(MouseScrolledEvent event) {
        zoomLevel -= event.getyOffset() * 0.25f;
        zoomLevel = Math.max(zoomLevel, 0.25f);
        camera.setProjection(-aspectRatio * zoomLevel, aspectRatio * zoomLevel, -zoomLevel, zoomLevel);
        return false;
    }

    private boolean onWindowResized(WindowResizeEvent event) {
        aspectRatio = (float) event.getWidth() / (float) event.getHeight();
        camera.setProjection(-aspectRatio * zoomLevel, aspectRatio * zoomLevel, -zoomLevel, zoomLevel);
        return false;
    }
}
