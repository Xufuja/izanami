package dev.xfj.engine.scripting;

import dev.xfj.engine.core.UUID;
import dev.xfj.engine.scene.Entity;
import org.graalvm.polyglot.Value;

public class ScriptInstance {
    private final ScriptClass scriptClass;
    private final Value instance;
    private final String onCreateMethod;
    private final String onUpdateMethod;

    public ScriptInstance(ScriptClass scriptClass, Entity entity) {
        this.scriptClass = scriptClass;
        this.onCreateMethod = scriptClass.getMethod("onCreate", 0);
        this.onUpdateMethod = scriptClass.getMethod("onUpdate", 1);

        UUID entityId = entity.getUUID();
        scriptClass.getMethod("constructor", 1);
        this.instance = scriptClass.instantiate(entityId);
    }

    public void invokeOnCreate() {
        if (onCreateMethod != null && !onCreateMethod.isEmpty() && !onCreateMethod.isBlank()) {
            scriptClass.invokeMethod(instance, onCreateMethod);
        }
    }

    public void invokeOnUpdate(float ts) {
        if (onUpdateMethod != null && !onUpdateMethod.isEmpty() && !onUpdateMethod.isBlank()) {
            scriptClass.invokeMethod(instance, onUpdateMethod, ts);
        }
    }
}
