package dev.xfj.engine.scene.components;

import dev.xfj.engine.scene.SceneCamera;

public class CameraComponent implements Component {
    public SceneCamera camera;
    public boolean primary;
    public boolean fixedAspectRatio;

    public CameraComponent() {
        this.camera = new SceneCamera();
        this.primary = true;
        this.fixedAspectRatio = false;
    }
}
