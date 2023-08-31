package dev.xfj.engine.scene.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import org.joml.Vector2f;

public class CircleCollider2DComponent implements Component {
    public Vector2f offset;
    public float radius;
    public float density;
    public float friction;
    public float restitution;
    public float restitutionThreshold;
    public Fixture runtimeFixture;

    public CircleCollider2DComponent() {
        this.offset = new Vector2f(0.0f, 0.0f);
        this.radius = 0.5f;
        this.density = 1.0f;
        this.friction = 0.5f;
        this.restitution = 0.0f;
        this.restitutionThreshold = 0.5f;
    }

    public CircleCollider2DComponent(Vector2f offset, float radius, float density, float friction, float restitution, float restitutionThreshold) {
        this(offset, radius, density, friction, restitution, restitutionThreshold, null);
    }

    public CircleCollider2DComponent(Vector2f offset, float radius, float density, float friction, float restitution, float restitutionThreshold, Fixture runtimeFixture) {
        this.offset = offset;
        this.radius = radius;
        this.density = density;
        this.friction = friction;
        this.restitution = restitution;
        this.restitutionThreshold = restitutionThreshold;
        this.runtimeFixture = runtimeFixture;
    }

    public CircleCollider2DComponent(CircleCollider2DComponent other) {
        this.offset = other.offset;
        this.radius = other.radius;
        this.density = other.density;
        this.friction = other.friction;
        this.restitution = other.restitution;
        this.restitutionThreshold = other.restitutionThreshold;
        this.runtimeFixture = other.runtimeFixture;
    }
}
