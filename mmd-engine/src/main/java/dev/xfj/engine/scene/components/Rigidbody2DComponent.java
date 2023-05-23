package dev.xfj.engine.scene.components;

import com.badlogic.gdx.physics.box2d.Body;

public class Rigidbody2DComponent implements Component {
    public BodyType type = BodyType.Static;
    public boolean fixedRotation = false;
    public Body runtimeBody = null;

    public enum BodyType {
        Static,
        Dynamic,
        Kinematic
    }
}
