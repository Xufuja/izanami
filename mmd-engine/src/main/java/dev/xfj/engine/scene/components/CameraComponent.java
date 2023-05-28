package dev.xfj.engine.scene.components;

import dev.xfj.engine.scene.SceneCamera;

public class CameraComponent implements Component {
    public SceneCamera camera;
    public boolean primary;
    public boolean fixedAspectRatio;

    public CameraComponent() {
        this(new SceneCamera(), true, false);
    }

    public CameraComponent(SceneCamera camera, boolean primary, boolean fixedAspectRatio) {
        this.camera = camera;
        this.primary = primary;
        this.fixedAspectRatio = fixedAspectRatio;
    }

    public CameraComponent(CameraComponent other) {
        this.camera = other.camera;
        this.primary = other.primary;
        this.fixedAspectRatio = other.fixedAspectRatio;
    }
}
