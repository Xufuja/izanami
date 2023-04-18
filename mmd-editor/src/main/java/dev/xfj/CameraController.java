package dev.xfj;

import dev.xfj.engine.core.Input;
import dev.xfj.engine.core.KeyCodes;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.scene.ScriptableEntity;
import dev.xfj.engine.scene.components.TransformComponent;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Random;

public class CameraController extends ScriptableEntity {
    public CameraController() {
        //Needs a default constructor to be able to do clazz.getDeclaredConstructor().newInstance();
    }

    @Override
    public void onCreate() {
        Matrix4f transform = getComponent(TransformComponent.class).transform;
        transform.set(3, 0, new Random().nextInt(10) - 5.0f);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onUpdate(TimeStep ts) {
        Vector4f cameraTransform = getComponent(TransformComponent.class).transform.getColumn(3, new Vector4f());
        float speed = 5.0f;
        float[] newCameraTransform = {cameraTransform.x, cameraTransform.y, cameraTransform.z};

        if (Input.isKeyPressed(KeyCodes.A)) {
            newCameraTransform[0] = newCameraTransform[0] - speed * ts.getTime();
        }
        if (Input.isKeyPressed(KeyCodes.D)) {
            newCameraTransform[0] = newCameraTransform[0] + speed * ts.getTime();
        }
        if (Input.isKeyPressed(KeyCodes.W)) {
            newCameraTransform[1] = newCameraTransform[1] + speed * ts.getTime();
        }
        if (Input.isKeyPressed(KeyCodes.S)) {
            newCameraTransform[1] = newCameraTransform[1] - speed * ts.getTime();
        }
        getComponent(TransformComponent.class).transform.setColumn(3, new Vector4f(newCameraTransform[0], newCameraTransform[1], newCameraTransform[2], cameraTransform.w));
    }
}
