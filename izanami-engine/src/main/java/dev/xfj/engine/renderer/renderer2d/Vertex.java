package dev.xfj.engine.renderer.renderer2d;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public sealed interface Vertex permits QuadVertex, CircleVertex, LineVertex, TextVertex {
    ByteBuffer getAsBuffer();

    default ByteBuffer getAsBuffer(Object object) {
        Class<?> currentClass = object.getClass();
        ByteBuffer buffer = ByteBuffer.allocateDirect(getVertexSize(currentClass)).order(ByteOrder.nativeOrder());

        for (Field field : currentClass.getDeclaredFields()) {
            Object fieldValue;
            try {
                field.setAccessible(true);
                fieldValue = field.get(object);
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

    static int getFloatArrayCount(Class<?> clazz) {
        int size = 0;
        for (Field field : clazz.getDeclaredFields()) {
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

    static int getIntArrayCount(Class<?> clazz) {
        int size = 0;
        for (Field field : clazz.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                size += 1;
            }
        }
        return size;
    }

    static int getVertexSize(Class<?> clazz) {
        return (getFloatArrayCount(clazz) * Float.BYTES) + (getIntArrayCount(clazz) * Integer.BYTES);
    }
}
