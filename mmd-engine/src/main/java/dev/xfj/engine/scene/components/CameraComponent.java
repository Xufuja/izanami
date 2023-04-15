package dev.xfj.engine.scene.components;

import dev.xfj.engine.renderer.Camera;
import org.joml.Matrix4f;

public class CameraComponent {
    public Camera camera;
    public boolean primary;

    public CameraComponent(Matrix4f projection) {
        this.camera = new Camera(projection);
        this.primary = true;
    }
}
