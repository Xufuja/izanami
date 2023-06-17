package dev.xfj.engine.scripting;

import org.joml.Vector3f;

public class ScriptGlue {
    public static void nativeLog(String string, int parameter) {
        System.out.println(String.format("%1$s, %2$d", string, parameter));
    }
    public static void nativeLog(Vector3f vector) {
        System.out.println(String.format("%1$s, %2$s, %3$s", vector.x, vector.y, vector.z));
    }
}
