package dev.xfj.platform.opengl;

import dev.xfj.engine.renderer.UniformBuffer;
import org.lwjgl.opengl.GL45;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL45.*;

public class OpenGLUniformBuffer implements UniformBuffer {
    private final int rendererId;

    public OpenGLUniformBuffer(int size, int binding) {
        this.rendererId = GL45.glCreateBuffers();
        GL45.glNamedBufferData(rendererId, size, GL_DYNAMIC_DRAW);
        GL45.glBindBufferBase(GL_UNIFORM_BUFFER, binding, rendererId);
    }

    @Override
    public void setData(ByteBuffer data, int offset) {
        GL45.glNamedBufferSubData(rendererId, offset, data);
    }
}
