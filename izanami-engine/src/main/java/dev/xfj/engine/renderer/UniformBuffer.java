package dev.xfj.engine.renderer;

import dev.xfj.engine.core.Log;
import dev.xfj.platform.opengl.OpenGLUniformBuffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface UniformBuffer {
    static UniformBuffer create(int size, int binding) {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLUniformBuffer(size, binding);
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }

    default void setData(FloatBuffer data) {
        setData(data, 0);
    }

    void setData(FloatBuffer data, int offset);
}
