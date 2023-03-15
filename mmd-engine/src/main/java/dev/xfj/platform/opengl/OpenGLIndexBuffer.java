package dev.xfj.platform.opengl;

import dev.xfj.engine.renderer.buffer.IndexBuffer;
import org.lwjgl.opengl.GL41;

import static org.lwjgl.opengl.GL41.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL41.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class OpenGLIndexBuffer implements IndexBuffer {
    private final int renderId;
    private final int count;

    public OpenGLIndexBuffer(int[] indices, int count) {
        this.renderId = glCreateBuffers();
        this.count = count;
        GL41.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, renderId);
        GL41.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }
    @Override
    public void bind() {
        GL41.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, renderId);
    }

    @Override
    public void unbind() {
        GL41.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public int getCount() {
        return this.count;
    }
}
