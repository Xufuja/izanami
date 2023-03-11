package dev.xfj.core.renderer.buffer;

import dev.xfj.core.Log;
import dev.xfj.core.renderer.Renderer;
import dev.xfj.platform.opengl.OpenGLVertexBuffer;

public interface VertexBuffer {
    static VertexBuffer create(float[] vertices) {
        switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                return null;
            }
            case OpenGL -> {
                return  new OpenGLVertexBuffer(vertices);
            }
        }
        Log.error("Unknown RendererAPI!");
        return null;
    }

    void bind();

    void unbind();
}
