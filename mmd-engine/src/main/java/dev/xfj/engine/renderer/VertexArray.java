package dev.xfj.engine.renderer;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.buffer.IndexBuffer;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import dev.xfj.platform.opengl.OpenGLVertexArray;

import java.util.List;

public interface VertexArray {
    static VertexArray create() {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLVertexArray();
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }

    void bind();

    void unbind();

    void addVertexBuffer(VertexBuffer vertexBuffer);

    void setIndexBuffer(IndexBuffer indexBuffer);

    List<VertexBuffer> getVertexBuffers();

    IndexBuffer getIndexBuffer();
}
