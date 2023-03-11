package dev.xfj.core.renderer.buffer;

import dev.xfj.core.Log;
import dev.xfj.core.renderer.Renderer;
import dev.xfj.platform.opengl.OpenGLIndexBuffer;

public interface IndexBuffer {
    static IndexBuffer create(int[] indices, int count) {
        switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                return null;
            }
            case OpenGL -> {
                return new OpenGLIndexBuffer(indices, count);
            }
        }
        Log.error("Unknown RendererAPI!");
        return null;
    }

    void bind();

    void unbind();

    int getCount();

}
