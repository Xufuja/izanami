package dev.xfj.engine.scene.components;

import org.joml.Vector4f;

public class CircleRendererComponent implements Component {
    public Vector4f color;
    public float thickness;
    public float fade;

    public CircleRendererComponent() {
        this(new Vector4f(1.0f), 1.0f, 0.00f);
    }

    public CircleRendererComponent(Vector4f color, float thickness, float fade) {
        this.color = color;
        this.thickness = thickness;
        this.fade = fade;
    }

    public CircleRendererComponent(CircleRendererComponent other) {
        this.color = new Vector4f(other.color);
        this.thickness = other.thickness;
        this.fade = other.fade;
    }
}
