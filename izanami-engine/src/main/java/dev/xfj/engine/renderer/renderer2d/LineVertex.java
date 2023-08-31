package dev.xfj.engine.renderer.renderer2d;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class LineVertex implements Vertex {
    public Vector3f position;
    public Vector4f color;
    public int entityId;

    public void setLineVertex(Vector3f position, Vector4f color, int entityId) {
        this.position = position;
        this.color = color;
        this.entityId = entityId;
    }

    @Override
    public ByteBuffer getAsBuffer() {
        return getAsBuffer(this);
    }

    public static int getFloatArrayCount() {
        return Vertex.getFloatArrayCount(LineVertex.class);
    }

    public static int getIntArrayCount() {
        return Vertex.getIntArrayCount(LineVertex.class);
    }

    public static int getVertexSize() {
        return Vertex.getVertexSize(LineVertex.class);
    }
}
