package dev.xfj.engine.renderer.renderer2d;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class QuadVertex {
    public Vector3f position;
    public Vector4f color;
    public Vector2f texCoord;
    public float texIndex;
    public float tilingFactor;
    public int entityId;

    public void setQuadVertex(Vector3f position, Vector4f color, Vector2f texCoord, float texIndex, float tilingFactor, int entityId) {
        this.position = position;
        this.color = color;
        this.texCoord = texCoord;
        this.texIndex = texIndex;
        this.tilingFactor = tilingFactor;
        this.entityId = entityId;
    }

    public ByteBuffer getAsBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(getQuadVertexSize()).order(ByteOrder.nativeOrder());

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
        for (Field field : QuadVertex.class.getDeclaredFields()) {
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
        for (Field field : QuadVertex.class.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                size += 1;
            }
        }
        return size;
    }

    public static int getQuadVertexSize() {
        return (getFloatArrayCount() * Float.BYTES) + (getIntArrayCount() * Integer.BYTES);
    }
}
