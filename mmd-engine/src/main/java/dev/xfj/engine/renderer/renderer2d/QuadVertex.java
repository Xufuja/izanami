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

    public void setQuadVertex(Vector3f position, Vector4f color, Vector2f texCoord, float texIndex, float tilingFactor) {
        this.position = position;
        this.color = color;
        this.texCoord = texCoord;
        this.texIndex = texIndex;
        this.tilingFactor = tilingFactor;
    }

    public ArrayList<Float> toList() {
        ArrayList<Float> list = new ArrayList<>();
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
                if (fieldValueClass.equals(float.class) || fieldValueClass.equals(Float.class)) {
                    list.add((Float) fieldValue);
                } else if (fieldValueClass.equals(Vector2f.class)) {
                    list.add(((Vector2f) fieldValue).x);
                    list.add(((Vector2f) fieldValue).y);
                } else if (fieldValueClass.equals(Vector3f.class)) {
                    list.add(((Vector3f) fieldValue).x);
                    list.add(((Vector3f) fieldValue).y);
                    list.add(((Vector3f) fieldValue).z);
                } else if (fieldValueClass.equals(Vector4f.class)) {
                    list.add(((Vector4f) fieldValue).x);
                    list.add(((Vector4f) fieldValue).y);
                    list.add(((Vector4f) fieldValue).z);
                    list.add(((Vector4f) fieldValue).w);
                }
            }
        }
        return list;
    }


    public static int getQuadVertexSize() {
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
