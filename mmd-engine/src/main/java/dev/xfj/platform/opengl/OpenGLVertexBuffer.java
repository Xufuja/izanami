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
    public void setData(ArrayList<ByteBuffer> data, int floatSize, int intSize) {
        GL45.glBindBuffer(GL_ARRAY_BUFFER, rendererId);

        int floatLength = floatSize * Float.BYTES;
        int intLength = intSize * Integer.BYTES;

        float[] floatArray = new float[floatSize];
        int[] intArray = new int[intSize];

        int currentOffset = 0;

        for (ByteBuffer buffer : data) {
            for (int i = 0; i < floatSize; i++) {
                floatArray[i] = buffer.getFloat();
            }

            GL45.glBufferSubData(GL_ARRAY_BUFFER, currentOffset, floatArray);
            currentOffset += floatLength;

            for (int i = 0; i < intSize; i++) {
                intArray[i] = buffer.getInt();
            }

            GL45.glBufferSubData(GL_ARRAY_BUFFER, currentOffset, intArray);
            currentOffset += intLength;
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
