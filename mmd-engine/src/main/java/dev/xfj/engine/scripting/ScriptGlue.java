package dev.xfj.engine.scripting;

import dev.xfj.engine.core.Log;
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
}
