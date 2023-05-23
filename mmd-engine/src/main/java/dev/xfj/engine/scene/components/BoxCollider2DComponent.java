package dev.xfj.engine.scene.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import org.joml.Vector2f;

public class BoxCollider2DComponent implements Component {
    public Vector2f offset;
    public Vector2f size;
    public float density;
    public float friction;
    public float restitution;
    public float restitutionThreshold;
    public Fixture runtimeFixture;

    public BoxCollider2DComponent() {
        offset = new Vector2f(0.0f, 0.0f);
        size = new Vector2f(0.5f, 0.5f);
        density = 1.0f;
        friction = 0.5f;
        restitution = 0.0f;
        restitutionThreshold = 0.5f;
        runtimeFixture = null;
    }
}
