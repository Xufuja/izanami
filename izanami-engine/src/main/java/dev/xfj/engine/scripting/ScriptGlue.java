package dev.xfj.engine.scripting;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import dev.xfj.engine.core.Input;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.UUID;
import dev.xfj.engine.physics.Physics2D;
import dev.xfj.engine.scene.Entity;
import dev.xfj.engine.scene.Scene;
import dev.xfj.engine.scene.components.Rigidbody2DComponent;
import dev.xfj.engine.scene.components.TransformComponent;
import org.graalvm.polyglot.Value;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class ScriptGlue {
    public static void nativeLog(String string, int parameter) {
        System.out.println(String.format("%1$s, %2$d", string, parameter));
    }

    public static Vector3f nativeLog(Vector3f parameter, Vector3f outResult) {
        Log.warn(String.format("Value: %1$s, %2$s, %3$s", parameter.x, parameter.y, parameter.z));
        outResult = parameter.normalize(new Vector3f());
        return outResult;
    }

    public static float nativeLog(Vector3f parameter) {
        Log.warn(String.format("Value: %1$s, %2$s, %3$s", parameter.x, parameter.y, parameter.z));
        return new Vector3f(parameter).dot(parameter);
    }

    //For all the below, add some sort of exception if either Scene or Entity is null

    public static boolean entityHasComponent(long entityId, Class<?> componentType) {
        Scene scene = ScriptEngine.getSceneContext();
        Entity entity = scene.getEntityByUUID(new UUID(entityId));
        return entity.hasComponent(componentType);
    }

    public static long entityFindEntityByName(String name) {
        Scene scene = ScriptEngine.getSceneContext();
        //Some sort of exception if scene is null
        Entity entity = scene.findEntityByName(name);

        if (entity == null) {
            return 0;
        }

        return entity.getUUID().getUUID();
    }

    public static Value getScriptInstance(long entityId) {
        return ScriptEngine.getManagedInstance(new UUID(entityId));
    }

    public static Vector3f transformComponentGetTranslation(long entityId) {
        Scene scene = ScriptEngine.getSceneContext();
        Entity entity = scene.getEntityByUUID(new UUID(entityId));
        return entity.getComponent(TransformComponent.class).translation;
    }
    public static Vector3f transformComponentSetTranslation(long entityId, Vector3f translation) {
        Scene scene = ScriptEngine.getSceneContext();
        Entity entity = scene.getEntityByUUID((new UUID(entityId)));
        entity.getComponent(TransformComponent.class).translation = translation;
        return entity.getComponent(TransformComponent.class).translation;
    }
    public static void rigidbody2DComponentApplyLinearImpulse(long entityId, Vector2f impulse, Vector2f point, boolean wake) {
        Scene scene = ScriptEngine.getSceneContext();
        Entity entity = scene.getEntityByUUID((new UUID(entityId)));
        Rigidbody2DComponent rb2d = entity.getComponent(Rigidbody2DComponent.class);
        Body body = rb2d.runtimeBody;
        body.applyLinearImpulse(new Vector2(impulse.x, impulse.y), new Vector2(point.x, point.y), wake);
    }
    public static void rigidbody2DComponentApplyLinearImpulseToCenter(long entityId, Vector2f impulse, boolean wake) {
        Scene scene = ScriptEngine.getSceneContext();
        Entity entity = scene.getEntityByUUID((new UUID(entityId)));
        Rigidbody2DComponent rb2d = entity.getComponent(Rigidbody2DComponent.class);
        Body body = rb2d.runtimeBody;
        //The applyLinearImpulseToCenter method does not exist in this version of Box2D
        //body.applyLinearImpulseToCenter(new Vector2(impulse.x, impulse.y),  wake);
    }
    public static Vector2f rigidbody2DComponentGetLinearVelocity(long entityId, Vector2f outLinearVelocity) {
        Scene scene = ScriptEngine.getSceneContext();
        Entity entity = scene.getEntityByUUID((new UUID(entityId)));
        Rigidbody2DComponent rb2d = entity.getComponent(Rigidbody2DComponent.class);
        Body body = rb2d.runtimeBody;
        Vector2 linearVelocity  = body.getLinearVelocity();
        outLinearVelocity = new Vector2f(linearVelocity.x, linearVelocity.y);

        return outLinearVelocity;
    }
    public static Rigidbody2DComponent.BodyType rigidbody2DComponentGetType(long entityId) {
        Scene scene = ScriptEngine.getSceneContext();
        Entity entity = scene.getEntityByUUID((new UUID(entityId)));
        Rigidbody2DComponent rb2d = entity.getComponent(Rigidbody2DComponent.class);
        Body body = rb2d.runtimeBody;

        return Physics2D.rigidbody2DTypeFromBox2DBody(body.getType());
    }
    public static void rigidbody2DComponentSetType(long entityId, Rigidbody2DComponent.BodyType bodyType) {
        Scene scene = ScriptEngine.getSceneContext();
        Entity entity = scene.getEntityByUUID((new UUID(entityId)));
        Rigidbody2DComponent rb2d = entity.getComponent(Rigidbody2DComponent.class);
        Body body = rb2d.runtimeBody;
        body.setType(Physics2D.rigidbody2DTypeToBox2DBody(bodyType));
    }
    public static boolean inputIsKeyDown(int keyCode) {
        return Input.isKeyPressed(keyCode);
    }
}
