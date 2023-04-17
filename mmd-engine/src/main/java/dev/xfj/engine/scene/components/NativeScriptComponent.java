package dev.xfj.engine.scene.components;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.scene.ScriptableEntity;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NativeScriptComponent<T extends ScriptableEntity> {
    //Doing it this way to replace the templating logic in C++
    public T instance;

    public Runnable instantiateFunction;
    public Runnable destroyInstanceFunction;

    public Consumer<ScriptableEntity> onCreateFunction;
    public Consumer<ScriptableEntity> onDestroyFunction;
    public BiConsumer<ScriptableEntity, TimeStep> onUpdateFunction;

    public void bind(Class<T> clazz) {
        instantiateFunction = () -> {
            try {
                instance = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                //Some sort of exception
                Log.error(e.getMessage());
            }
        };
        destroyInstanceFunction = () -> instance = null;

        onCreateFunction = ScriptableEntity::onCreate;
        onDestroyFunction = ScriptableEntity::onDestroy;
        onUpdateFunction = ScriptableEntity::onUpdate;
    }
}
