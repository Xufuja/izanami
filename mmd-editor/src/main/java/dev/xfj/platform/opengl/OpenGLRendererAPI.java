package dev.xfj.platform.opengl;

import dev.xfj.core.renderer.RendererAPIBase;
import dev.xfj.core.renderer.VertexArray;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL41;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL41.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL41C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLRendererAPI extends RendererAPIBase {

    @Override
    public void setClearColor(Vector4f color) {
        GL41.glClearColor(color.x, color.y, color.z, color.z);
    }

    @Override
    public void clear() {
        GL41.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void drawIndexed(VertexArray vertexArray) {
        GL41.glDrawElements(GL_TRIANGLES, vertexArray.getIndexBuffer().getCount(), GL_UNSIGNED_INT, NULL);
    }
}
