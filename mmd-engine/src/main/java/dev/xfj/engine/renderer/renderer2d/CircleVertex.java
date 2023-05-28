package dev.xfj.engine.renderer.renderer2d;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CircleVertex {
    public Vector3f worldPosition;
    public Vector3f localPosition;
    public Vector4f color;
    public float thickness;
    public float fade;
    public int entityId;

    public void setCircleVertex(Vector3f worldPosition, Vector3f localPosition, Vector4f color, float thickness, float fade, int entityId) {
        this.worldPosition = worldPosition;
        this.localPosition = localPosition;
        this.color = color;
        this.thickness = thickness;
        this.fade = fade;
        this.entityId = entityId;
    }

    public ByteBuffer getAsBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(getVertexSize()).order(ByteOrder.nativeOrder());

        for (Field field : this.getClass().getDeclaredFields()) {
            Object fieldValue;
            try {
                field.setAccessible(true);
                fieldValue = field.get(this);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (fieldValue != null) {
                Class<?> fieldValueClass = fieldValue.getClass();
                if (fieldValueClass.equals(Float.class)) {
                    buffer.putFloat((float) fieldValue);
                } else if (fieldValueClass.equals(Vector2f.class)) {
                    buffer.putFloat(((Vector2f) fieldValue).x);
                    buffer.putFloat(((Vector2f) fieldValue).y);
                } else if (fieldValueClass.equals(Vector3f.class)) {
                    buffer.putFloat(((Vector3f) fieldValue).x);
                    buffer.putFloat(((Vector3f) fieldValue).y);
                    buffer.putFloat(((Vector3f) fieldValue).z);
                } else if (fieldValueClass.equals(Vector4f.class)) {
                    buffer.putFloat(((Vector4f) fieldValue).x);
                    buffer.putFloat(((Vector4f) fieldValue).y);
                    buffer.putFloat(((Vector4f) fieldValue).z);
                    buffer.putFloat(((Vector4f) fieldValue).w);
                } else if (fieldValueClass.equals(Integer.class)) {
                    buffer.putInt((int) fieldValue);
                }
            }
        }
        buffer.rewind();
        return buffer;
    }

    public static int getFloatArrayCount() {
        int size = 0;
        for (Field field : CircleVertex.class.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
                size += 1;
            } else if (fieldType.equals(Vector2f.class)) {
                size += 2;
            } else if (fieldType.equals(Vector3f.class)) {
                size += 3;
            } else if (fieldType.equals(Vector4f.class)) {
                size += 4;
            }
        }
        return size;
    }

    public static int getIntArrayCount() {
        int size = 0;
        for (Field field : CircleVertex.class.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                size += 1;
            }
        }
        return size;
    }

    public static int getVertexSize() {
        return (getFloatArrayCount() * Float.BYTES) + (getIntArrayCount() * Integer.BYTES);
    }
}
