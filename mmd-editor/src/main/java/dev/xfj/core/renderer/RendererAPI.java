package dev.xfj.core.renderer;

import org.joml.Vector4f;

public interface RendererAPI {
    void setClearColor(Vector4f color);
    void clear();
    void drawIndexed(VertexArray vertexArray);


}
