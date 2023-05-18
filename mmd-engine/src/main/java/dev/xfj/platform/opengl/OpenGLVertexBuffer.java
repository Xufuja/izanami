package dev.xfj.platform.opengl;

import dev.xfj.engine.renderer.buffer.BufferLayout;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import org.lwjgl.opengl.GL45;

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
    public void setData(float[] data) {
        GL45.glBindBuffer(GL_ARRAY_BUFFER, rendererId);
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
