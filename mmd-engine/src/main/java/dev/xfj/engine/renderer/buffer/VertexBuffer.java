package dev.xfj.engine.renderer.buffer;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.Renderer;
import dev.xfj.platform.opengl.OpenGLVertexBuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public interface VertexBuffer {
    static VertexBuffer create(int size) {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLVertexBuffer(size);
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }
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

    void setData(ArrayList<ByteBuffer> data, int floatSize, int intSize);

    BufferLayout getLayout();

    void setLayout(BufferLayout layout);
}
