package dev.xfj.engine.renderer;

import dev.xfj.engine.core.Log;
import dev.xfj.platform.opengl.OpenGLRendererAPI;
import dev.xfj.platform.opengl.OpenGLTexture2D;
import org.joml.Vector4f;

public interface RendererAPI {

    static RendererAPI create() {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLRendererAPI();
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }

    void init();

    void setViewport(int x, int y, int width, int height);

    void setClearColor(Vector4f color);

    void clear();

    void drawIndexed(VertexArray vertexArray);
    void drawIndexed(VertexArray vertexArray, int indexCount);


}
