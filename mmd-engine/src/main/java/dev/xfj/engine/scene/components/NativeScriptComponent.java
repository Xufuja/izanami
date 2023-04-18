package dev.xfj.engine.scene.components;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.scene.ScriptableEntity;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NativeScriptComponent<T extends ScriptableEntity> {
    //Doing it this way to replace the templating logic in C++
    public T instance;

    public Supplier<ScriptableEntity> instantiateScript;
    public Consumer<NativeScriptComponent<?>> destroyScript;

    public void bind(Class<T> clazz) {
        instantiateScript = () -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                //Some sort of exception
                Log.error(e.getMessage());
                return null;
            }
        };
        destroyScript = nsc -> nsc.instance = null;
    }
}
