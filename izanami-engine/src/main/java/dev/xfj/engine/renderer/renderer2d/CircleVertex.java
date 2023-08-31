package dev.xfj.engine.renderer.renderer2d;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class CircleVertex implements Vertex{
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

    @Override
    public ByteBuffer getAsBuffer() {
        return getAsBuffer(this);
    }

    public static int getFloatArrayCount() {
        return Vertex.getFloatArrayCount(CircleVertex.class);
    }

    public static int getIntArrayCount() {
        return Vertex.getIntArrayCount(CircleVertex.class);
    }

    public static int getVertexSize() {
        return Vertex.getVertexSize(CircleVertex.class);
    }
}
