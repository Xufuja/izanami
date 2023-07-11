package dev.xfj.engine.scripting;

import dev.xfj.engine.core.UUID;
import dev.xfj.engine.scene.Entity;
import org.graalvm.polyglot.Value;

import java.util.Map;

public class ScriptInstance {
    private final ScriptClass scriptClass;
    private final Value instance;
    private final String onCreateMethod;
    private final String onUpdateMethod;
    private static Value fieldValueBuffer;

    public ScriptInstance(ScriptClass scriptClass, Entity entity) {
        this.scriptClass = scriptClass;
        this.onCreateMethod = scriptClass.getMethod("onCreate", 0);
        this.onUpdateMethod = scriptClass.getMethod("onUpdate", 1);

        UUID entityId = entity.getUUID();
        scriptClass.getMethod("constructor", 1);
        this.instance = scriptClass.instantiate(entityId.getUUID());
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

    public <T> T getFieldValue(Class<T> type, String name) {
        boolean success = getFieldValueInternal("name");

        if (success) {
            if (type == Float.class) {
                //So apparently you cannot directly cast double to float and everything with a decimal is always considered a double if it comes from JS
                //The Double class has the .floatValue() method so casting from the primitive to that
                //If I do not cast to T it complains about the class not being T
                return type.cast(((Double) fieldValueBuffer.asDouble()).floatValue());
            }
        }
        return null;
    }

    private boolean getFieldValueInternal(String name) {
        Map<String, ScriptField> fields = scriptClass.getFields();

        for (Map.Entry<String, ScriptField> entry : fields.entrySet()) {
            if (entry.getKey().equals(name)) {
                fieldValueBuffer = instance.getMember(name);
                return true;
            }
        }

        return false;
    }

    public ScriptClass getScriptClass() {
        return scriptClass;
    }
}
