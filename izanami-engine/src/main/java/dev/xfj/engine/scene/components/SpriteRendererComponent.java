package dev.xfj.engine.scene.components;

import dev.xfj.engine.renderer.texture.Texture2D;
import org.joml.Vector4f;

public class SpriteRendererComponent implements Component {
    public Vector4f color;
    public Texture2D texture;
    public float tilingFactor;

    public SpriteRendererComponent() {
        this(new Vector4f(1.0f));
    }

    public SpriteRendererComponent(Vector4f color) {
        this(color, 1.0f);
    }

    public SpriteRendererComponent(Vector4f color, float tilingFactor) {
        this.color = color;
        this.tilingFactor = tilingFactor;
    }

    public SpriteRendererComponent(Vector4f color, Texture2D texture, float tilingFactor) {
        this.color = color;
        this.texture = texture;
        this.tilingFactor = tilingFactor;
    }

    public SpriteRendererComponent(SpriteRendererComponent other) {
        this.color = new Vector4f(other.color);
        this.texture = other.texture;
        this.tilingFactor = other.tilingFactor;
    }
}
