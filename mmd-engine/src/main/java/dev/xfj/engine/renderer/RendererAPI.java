package dev.xfj.engine.renderer;

import org.joml.Vector4f;

public interface RendererAPI {
    void init();

    void setViewport(int x, int y, int width, int height);
    void setClearColor(Vector4f color);
    void clear();
    void drawIndexed(VertexArray vertexArray);


}
