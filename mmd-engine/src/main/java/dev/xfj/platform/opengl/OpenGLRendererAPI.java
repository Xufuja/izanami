package dev.xfj.platform.opengl;

import dev.xfj.engine.renderer.RendererAPIBase;
import dev.xfj.engine.renderer.VertexArray;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL45;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLRendererAPI extends RendererAPIBase {

    @Override
    public void init() {
        GL45.glEnable(GL_BLEND);
        GL45.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL45.glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void setViewport(int x, int y, int width, int height) {
        GL45.glViewport(x, y, width, height);
    }

    @Override
    public void setClearColor(Vector4f color) {
        GL45.glClearColor(color.x, color.y, color.z, color.z);
    }

    @Override
    public void clear() {
        GL45.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void drawIndexed(VertexArray vertexArray) {
        GL45.glDrawElements(GL_TRIANGLES, vertexArray.getIndexBuffer().getCount(), GL_UNSIGNED_INT, NULL);
    }
}
