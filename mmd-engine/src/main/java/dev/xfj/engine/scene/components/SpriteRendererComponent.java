package dev.xfj.engine.scene.components;

import org.joml.Vector4f;

public class SpriteRendererComponent {
    public Vector4f color;

    public SpriteRendererComponent() {
       this(new Vector4f(1.0f));
    }
    public SpriteRendererComponent(Vector4f color) {
        this.color = color;
    }
}
