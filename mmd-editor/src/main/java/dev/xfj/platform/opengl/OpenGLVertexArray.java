package dev.xfj.platform.opengl;

import dev.xfj.core.Log;
import dev.xfj.core.renderer.VertexArray;
import dev.xfj.core.renderer.buffer.BufferElement;
import dev.xfj.core.renderer.buffer.BufferLayout;
import dev.xfj.core.renderer.buffer.IndexBuffer;
import dev.xfj.core.renderer.buffer.VertexBuffer;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL45;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL20.GL_BOOL;


public class OpenGLVertexArray implements VertexArray {
    private final int renderId;
    private final List<VertexBuffer> vertexBuffers;
    private IndexBuffer indexBuffer;

    public static int shaderDataTypeToOpenGLBaseType(BufferElement.ShaderDataType type) {
        return switch (type) {
            case Float, Float3, Float2, Mat3, Mat4, Float4 -> GL_FLOAT;
            case Int, Int2, Int3, Int4 -> GL_INT;
            case Bool -> GL_BOOL;
            default -> {
                Log.error("Unknown ShaderDataType!");
                yield 0;
            }
        };
    }

    public OpenGLVertexArray() {
        this.renderId = GL45.glCreateVertexArrays();
        this.vertexBuffers = new ArrayList<>();
    }

    @Override
    public void bind() {
        GL45.glBindVertexArray(renderId);
    }

    @Override
    public void unbind() {
        GL45.glBindVertexArray(0);
    }

    @Override
    public void addVertexBuffer(VertexBuffer vertexBuffer) {
        //TODO Some sort of exception to replicate HZ_CORE_ASSERT(vertexBuffer->GetLayout().GetElements().size(), "Vertex Buffer has no layout!");
        GL45.glBindVertexArray(renderId);
        vertexBuffer.bind();

        int index = 0;
        BufferLayout layout = vertexBuffer.getLayout();
        for (BufferElement element : layout) {
            GL41.glEnableVertexAttribArray(index);
            GL41.glVertexAttribPointer(index, element.getComponent(), shaderDataTypeToOpenGLBaseType(element.getType()), element.isNormalized(), layout.getStride(), element.offset);
            index++;
        }
        this.vertexBuffers.add(vertexBuffer);
    }

    @Override
    public void setIndexBuffer(IndexBuffer indexBuffer) {
        GL45.glBindVertexArray(renderId);
        indexBuffer.bind();
        this.indexBuffer = indexBuffer;
    }

    @Override
    public List<VertexBuffer> getVertexBuffers() {
        return vertexBuffers;
    }

    @Override
    public IndexBuffer getIndexBuffer() {
        return indexBuffer;
    }
}
