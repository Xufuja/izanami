package dev.xfj.engine.scripting;

import dev.xfj.engine.core.UUID;
import dev.xfj.engine.scene.Entity;
import org.graalvm.polyglot.Value;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
        boolean success = getFieldValueInternal(name);

        if (success) {
            if (type == Float.class) {
                return type.cast(Float.parseFloat(fieldValueBuffer.toString()));
            } else if (type == Double.class) {
                return type.cast(fieldValueBuffer.asDouble());
            } else if (type == Boolean.class) {
                return type.cast(fieldValueBuffer.asBoolean());
            } else if (type == Character.class) {
                return type.cast(fieldValueBuffer.as(Character.class));
            } else if (type == Byte.class) {
                return type.cast(fieldValueBuffer.asByte());
            } else if (type == Short.class) {
                return type.cast(fieldValueBuffer.asShort());
            } else if (type == Integer.class) {
                return type.cast(fieldValueBuffer.asInt());
            } else if (type == Long.class) {
                return type.cast(fieldValueBuffer.asLong());
            } else if (type == Vector2f.class) {
                return type.cast(fieldValueBuffer.as(Vector2f.class));
            } else if (type == Vector3f.class) {
                return type.cast(fieldValueBuffer.as(Vector3f.class));
            } else if (type == Vector4f.class) {
                return type.cast(fieldValueBuffer.as(Vector4f.class));
            }

        }

        try {
            if (Number.class.isAssignableFrom(type)) {
                Method method = type.getDeclaredMethod("valueOf", String.class);
                method.setAccessible(true);
                return type.cast(method.invoke(null, "0"));
            } else {
                Constructor<T> constructor = type.getConstructor(type);
                constructor.setAccessible(true);
                return constructor.newInstance(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setFieldValue(String name, Object value) {
        setFieldValueInternal(name, value);
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

    private boolean setFieldValueInternal(String name, Object value) {
        Map<String, ScriptField> fields = scriptClass.getFields();

        for (Map.Entry<String, ScriptField> entry : fields.entrySet()) {
            if (entry.getKey().equals(name)) {
                instance.putMember(name, value);
                return true;
            }
        }

        return false;
    }

    public ScriptClass getScriptClass() {
        return scriptClass;
    }
}
