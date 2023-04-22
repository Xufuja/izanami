package dev.xfj;

import dev.xfj.engine.core.Input;
import dev.xfj.engine.core.KeyCodes;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.scene.ScriptableEntity;
import dev.xfj.engine.scene.components.TransformComponent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Random;

public class CameraController extends ScriptableEntity {
    public CameraController() {
        //Needs a default constructor to be able to do clazz.getDeclaredConstructor().newInstance();
    }

    @Override
    public void onCreate() {
        Vector3f translation = getComponent(TransformComponent.class).translation;
        translation.x = new Random().nextInt(10) - 5.0f;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onUpdate(TimeStep ts) {
        Vector3f translation = getComponent(TransformComponent.class).translation;

        float speed = 5.0f;

        if (Input.isKeyPressed(KeyCodes.A)) {
            translation.x -= speed * ts.getTime();
        }
        if (Input.isKeyPressed(KeyCodes.D)) {
            translation.x += speed * ts.getTime();
        }
        if (Input.isKeyPressed(KeyCodes.W)) {
            translation.y += speed * ts.getTime();
        }
        if (Input.isKeyPressed(KeyCodes.S)) {
            translation.y -= speed * ts.getTime();
        }
    }
}
