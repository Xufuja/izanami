package dev.xfj.platform.opengl;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.buffer.BufferLayout;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import org.lwjgl.opengl.GL45;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL45.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL45.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class OpenGLVertexBuffer implements VertexBuffer {
    private final int rendererId;
    private BufferLayout layout;

    public OpenGLVertexBuffer(int size) {
        this.rendererId = GL45.glCreateBuffers();
        GL45.glBindBuffer(GL_ARRAY_BUFFER, rendererId);
        GL45.glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);

    }

    public OpenGLVertexBuffer(float[] vertices) {
        this.rendererId = glCreateBuffers();
        GL45.glBindBuffer(GL_ARRAY_BUFFER, rendererId);
        GL45.glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    }

    @Override
    public void bind() {
        GL45.glBindBuffer(GL_ARRAY_BUFFER, rendererId);
    }

    @Override
    public void unbind() {
        GL45.glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void setData(ArrayList<ByteBuffer> data, int floatCount, int intCount) {
        GL45.glBindBuffer(GL_ARRAY_BUFFER, rendererId);

        int floatSize = floatCount * Float.BYTES;
        int intSize = intCount * Integer.BYTES;

        float[] floatArray = new float[floatCount];
        int[] intArray = new int[intCount];

        int currentOffset = 0;

        for (ByteBuffer buffer : data) {
            for (int i = 0; i < floatCount; i++) {
                floatArray[i] = buffer.getFloat();
            }

            GL45.glBufferSubData(GL_ARRAY_BUFFER, currentOffset, floatArray);
            currentOffset += floatSize;

            for (int i = 0; i < intCount; i++) {
                intArray[i] = buffer.getInt();
            }

            GL45.glBufferSubData(GL_ARRAY_BUFFER, currentOffset, intArray);
            currentOffset += intSize;
        }
    }

    @Override
    public BufferLayout getLayout() {
        return this.layout;
    }

    @Override
    public void setLayout(BufferLayout layout) {
        this.layout = layout;
    }


}
