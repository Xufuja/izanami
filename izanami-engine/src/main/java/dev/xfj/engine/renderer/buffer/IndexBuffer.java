package dev.xfj.engine.renderer.buffer;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.Renderer;
import dev.xfj.platform.opengl.OpenGLIndexBuffer;

public interface IndexBuffer {
    static IndexBuffer create(int[] indices, int count) {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLIndexBuffer(indices, count);
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }

    void bind();

    void unbind();

    int getCount();

}
