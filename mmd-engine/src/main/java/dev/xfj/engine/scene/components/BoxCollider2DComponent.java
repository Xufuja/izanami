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
        this(new Vector2f(0.0f, 0.0f), new Vector2f(0.5f, 0.5f), 1.0f, 0.5f, 0.0f, 0.5f);
    }

    public BoxCollider2DComponent(Vector2f offset, Vector2f size, float density, float friction, float restitution, float restitutionThreshold) {
      this(offset, size, density, friction, restitution, restitutionThreshold, null);
    }

    public BoxCollider2DComponent(Vector2f offset, Vector2f size, float density, float friction, float restitution, float restitutionThreshold, Fixture runtimeFixture) {
        this.offset = offset;
        this.size = size;
        this.density = density;
        this.friction = friction;
        this.restitution = restitution;
        this.restitutionThreshold = restitutionThreshold;
        this.runtimeFixture = runtimeFixture;
    }

    public BoxCollider2DComponent(BoxCollider2DComponent other) {
        this.offset = new Vector2f(other.offset);
        this.size = new Vector2f(other.size);
        this.density = other.density;
        this.friction = other.friction;
        this.restitution = other.restitution;
        this.restitutionThreshold = other.restitutionThreshold;
        this.runtimeFixture = other.runtimeFixture;
    }
}
