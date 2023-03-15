package dev.xfj.engine.renderer.buffer;

import dev.xfj.engine.Log;
import dev.xfj.engine.renderer.Renderer;
import dev.xfj.platform.opengl.OpenGLVertexBuffer;

public interface VertexBuffer {
    static VertexBuffer create(float[] vertices) {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLVertexBuffer(vertices);
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }

    void bind();

    void unbind();

    BufferLayout getLayout();

    void setLayout(BufferLayout layout);
}
