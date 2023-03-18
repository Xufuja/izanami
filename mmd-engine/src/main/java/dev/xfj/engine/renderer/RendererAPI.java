package dev.xfj.engine.renderer;

import org.joml.Vector4f;

public interface RendererAPI {
    void init();
    void setClearColor(Vector4f color);
    void clear();
    void drawIndexed(VertexArray vertexArray);


}
