package dev.xfj.engine.physics;

import com.badlogic.gdx.physics.box2d.BodyDef;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.scene.components.Rigidbody2DComponent;

public class Physics2D {
    public static BodyDef.BodyType rigidbody2DTypeToBox2DBody(Rigidbody2DComponent.BodyType bodyType) {
        return switch (bodyType) {
            case Static -> BodyDef.BodyType.StaticBody;
            case Dynamic -> BodyDef.BodyType.DynamicBody;
            case Kinematic -> BodyDef.BodyType.KinematicBody;
            default -> {
                //Some sort of exception
                Log.error("Unknown body type");
                yield BodyDef.BodyType.StaticBody;
            }
        };
    }
    public static Rigidbody2DComponent.BodyType rigidbody2DTypeFromBox2DBody(BodyDef.BodyType bodyType) {
        return switch (bodyType) {
            case StaticBody ->  Rigidbody2DComponent.BodyType.Static;
            case DynamicBody -> Rigidbody2DComponent.BodyType.Dynamic;
            case KinematicBody -> Rigidbody2DComponent.BodyType.Kinematic;
            default -> {
                //Some sort of exception
                Log.error("Unknown body type");
                yield Rigidbody2DComponent.BodyType.Static;
            }
        };
    }
}
