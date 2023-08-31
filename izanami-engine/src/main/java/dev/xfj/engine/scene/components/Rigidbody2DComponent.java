package dev.xfj.engine.scene.components;

import com.badlogic.gdx.physics.box2d.Body;

public class Rigidbody2DComponent implements Component {
    public BodyType type;
    public boolean fixedRotation;
    public Body runtimeBody;

    public enum BodyType {
        Static,
        Dynamic,
        Kinematic
    }

    public Rigidbody2DComponent() {
        this(BodyType.Static, false);
    }

    public Rigidbody2DComponent(BodyType type, boolean fixedRotation) {
        this(type, fixedRotation, null);
    }

    public Rigidbody2DComponent(BodyType type, boolean fixedRotation, Body runtimeBody) {
        this.type = type;
        this.fixedRotation = fixedRotation;
        this.runtimeBody = runtimeBody;
    }

    public Rigidbody2DComponent(Rigidbody2DComponent other) {
        this.type = other.type;
        this.fixedRotation = other.fixedRotation;
        this.runtimeBody = other.runtimeBody;
    }
}
