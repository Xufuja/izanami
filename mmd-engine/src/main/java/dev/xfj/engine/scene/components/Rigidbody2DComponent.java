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
        type = BodyType.Static;
        fixedRotation = false;
        runtimeBody = null;
    }

    public Rigidbody2DComponent(Rigidbody2DComponent other) {
        this.type = other.type;
        this.fixedRotation = other.fixedRotation;
        this.runtimeBody = other.runtimeBody;
    }
}
