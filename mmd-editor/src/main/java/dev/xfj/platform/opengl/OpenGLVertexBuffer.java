package dev.xfj.platform.opengl;

import dev.xfj.core.renderer.buffer.BufferLayout;
import dev.xfj.core.renderer.buffer.VertexBuffer;
import org.lwjgl.opengl.GL41;

import static org.lwjgl.opengl.GL41.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL41.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class OpenGLVertexBuffer implements VertexBuffer {
    private final int renderId;
    private BufferLayout layout;

    public OpenGLVertexBuffer(float[] vertices) {
        this.renderId = glCreateBuffers();
        GL41.glBindBuffer(GL_ARRAY_BUFFER, renderId);
        GL41.glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    }
    @Override
    public void bind() {
        GL41.glBindBuffer(GL_ARRAY_BUFFER, renderId);
    }

    @Override
    public void unbind() {
        GL41.glBindBuffer(GL_ARRAY_BUFFER, 0);
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
