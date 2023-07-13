package dev.xfj.engine.scripting;

import org.graalvm.polyglot.Value;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ScriptFieldInstance {
    public ScriptField field;
    protected Value buffer;

    public <T> T getValue(Class<T> type) {
        if (type == Float.class) {
            return type.cast(Float.parseFloat(buffer.toString()));
        } else if (type == Double.class) {
            return type.cast(buffer.asDouble());
        } else if (type == Boolean.class) {
            return type.cast(buffer.asBoolean());
        } else if (type == Character.class) {
            return type.cast(buffer.as(Character.class));
        } else if (type == Byte.class) {
            return type.cast(buffer.asByte());
        } else if (type == Short.class) {
            return type.cast(buffer.asShort());
        } else if (type == Integer.class) {
            return type.cast(buffer.asInt());
        } else if (type == Long.class) {
            return type.cast(buffer.asLong());
        } else if (type == Vector2f.class) {
            return type.cast(buffer.as(Vector2f.class));
        } else if (type == Vector3f.class) {
            return type.cast(buffer.as(Vector3f.class));
        } else if (type == Vector4f.class) {
            return type.cast(buffer.as(Vector4f.class));
        } else {
            return null;
        }

    }

    public void setValue(Object value) {
        buffer = (Value) value;
    }

}
