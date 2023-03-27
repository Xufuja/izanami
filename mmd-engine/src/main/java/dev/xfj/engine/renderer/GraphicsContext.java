package dev.xfj.engine.renderer;

import dev.xfj.engine.core.Log;
import dev.xfj.platform.opengl.OpenGLContext;

public interface GraphicsContext {
    static GraphicsContext create(long window) {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLContext(window);
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }
    void init();
    void swapBuffers();
}
