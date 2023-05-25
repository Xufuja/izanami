package dev.xfj.engine.renderer.renderer2d;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class QuadVertex {
    public Vector3f position;
    public Vector4f color;
    public Vector2f texCoord;
    public float texIndex;
    public float tilingFactor;
    //The entityId is an int but this class assumes that everything is a float, it should fit
    public float entityId;

    public void setQuadVertex(Vector3f position, Vector4f color, Vector2f texCoord, float texIndex, float tilingFactor, float entityId) {
        this.position = position;
        this.color = color;
        this.texCoord = texCoord;
        this.texIndex = texIndex;
        this.tilingFactor = tilingFactor;
        this.entityId = entityId;
    }

    public float[] getFloatsAsArray() {
        float[] array = new float[getFloatArrayCount()];
        int arrayPosition = 0;

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
                    array[arrayPosition++] = (float) fieldValue;
                } else if (fieldValueClass.equals(Vector2f.class)) {
                    array[arrayPosition++] = ((Vector2f) fieldValue).x;
                    array[arrayPosition++] = ((Vector2f) fieldValue).y;
                } else if (fieldValueClass.equals(Vector3f.class)) {
                    array[arrayPosition++] = ((Vector3f) fieldValue).x;
                    array[arrayPosition++] = ((Vector3f) fieldValue).y;
                    array[arrayPosition++] = ((Vector3f) fieldValue).z;
                } else if (fieldValueClass.equals(Vector4f.class)) {
                    array[arrayPosition++] = ((Vector4f) fieldValue).x;
                    array[arrayPosition++] = ((Vector4f) fieldValue).y;
                    array[arrayPosition++] = ((Vector4f) fieldValue).z;
                    array[arrayPosition++] = ((Vector4f) fieldValue).w;
                }
            }
        }
        return array;
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
}
