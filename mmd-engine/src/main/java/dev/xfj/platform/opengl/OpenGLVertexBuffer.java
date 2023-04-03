package dev.xfj.platform.opengl;

import dev.xfj.engine.renderer.buffer.BufferLayout;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import org.lwjgl.opengl.GL45;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL45.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL45.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLVertexBuffer implements VertexBuffer {
    private final int renderId;
    private BufferLayout layout;

    public OpenGLVertexBuffer(int size) {
        this.renderId = glCreateBuffers();
        GL45.glBindBuffer(GL_ARRAY_BUFFER, renderId);
        GL45.glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);

    }
    public OpenGLVertexBuffer(float[] vertices) {
        this.renderId = glCreateBuffers();
        GL45.glBindBuffer(GL_ARRAY_BUFFER, renderId);
        GL45.glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    }
    @Override
    public void bind() {
        GL45.glBindBuffer(GL_ARRAY_BUFFER, renderId);
    }

    @Override
    public void unbind() {
        GL45.glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void setData(float[] data) {
        GL45.glBindBuffer(GL_ARRAY_BUFFER, renderId);
        GL45.glBufferSubData(GL_ARRAY_BUFFER, 0, data);
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
