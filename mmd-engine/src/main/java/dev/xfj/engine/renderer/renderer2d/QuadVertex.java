package dev.xfj.engine.renderer.renderer2d;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class QuadVertex implements Vertex {
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

    @Override
    public ByteBuffer getAsBuffer() {
        return getAsBuffer(this);
    }

    public static int getFloatArrayCount() {
        return Vertex.getFloatArrayCount(QuadVertex.class);
    }

    public static int getIntArrayCount() {
        return Vertex.getIntArrayCount(QuadVertex.class);
    }

    public static int getVertexSize() {
        return Vertex.getVertexSize(QuadVertex.class);
    }
}
