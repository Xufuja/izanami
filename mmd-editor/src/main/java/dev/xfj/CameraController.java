package dev.xfj;

import dev.xfj.engine.core.Input;
import dev.xfj.engine.core.KeyCodes;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.scene.ScriptableEntity;
import dev.xfj.engine.scene.components.TransformComponent;
import org.joml.Matrix4f;

public class CameraController extends ScriptableEntity {
    public CameraController() {
        //Needs a default constructor to be able to do clazz.getDeclaredConstructor().newInstance();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onUpdate(TimeStep ts) {
        Matrix4f transform = getComponent(TransformComponent.class).transform;
        float speed = 5.0f;

        if (Input.isKeyPressed(KeyCodes.MMD_KEY_A)) {
            transform.set(3, 0, transform.get(3, 0) - speed * ts.getTime());
        }
        if (Input.isKeyPressed(KeyCodes.MMD_KEY_D)) {
            transform.set(3, 0, transform.get(3, 0) + speed * ts.getTime());
        }
        if (Input.isKeyPressed(KeyCodes.MMD_KEY_W)) {
            transform.set(3, 1, transform.get(3, 1) + speed * ts.getTime());
        }
        if (Input.isKeyPressed(KeyCodes.MMD_KEY_S)) {
            transform.set(3, 1, transform.get(3, 1) - speed * ts.getTime());
        }
    }
}
