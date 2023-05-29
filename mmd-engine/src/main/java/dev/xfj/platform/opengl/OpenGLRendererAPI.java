package dev.xfj.platform.opengl;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.RendererAPIBase;
import dev.xfj.engine.renderer.VertexArray;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLDebugMessageCallback;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public class OpenGLRendererAPI extends RendererAPIBase {

    @Override
    public void init() {
        GL45.glEnable(GL_BLEND);
        GL45.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL45.glEnable(GL_DEPTH_TEST);
        GL45.glEnable(GL_LINE_SMOOTH);

        if (ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0) {
            GL45.glEnable(GL_DEBUG_OUTPUT);
            GL45.glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
            GLDebugMessageCallback callback = new GLDebugMessageCallback() {
                @Override
                public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
                    switch (severity) {
                        case GL_DEBUG_SEVERITY_HIGH, GL_DEBUG_SEVERITY_MEDIUM -> Log.error(memUTF8(message));
                        case GL_DEBUG_SEVERITY_LOW -> Log.warn(memUTF8(message));
                        case GL_DEBUG_SEVERITY_NOTIFICATION -> Log.trace(memUTF8(message));
                        default -> Log.error("Unknown severity!");
                    }
                }
            };
            GL45.glDebugMessageCallback(callback, NULL);
            GL45.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_NOTIFICATION, (IntBuffer) null, false);
        }

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
        drawIndexed(vertexArray, 0);
    }

    @Override
    public void drawIndexed(VertexArray vertexArray, int indexCount) {
        vertexArray.bind();
        int count = indexCount > 0 ? indexCount : vertexArray.getIndexBuffer().getCount();
        GL45.glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, NULL);
    }

    @Override
    public void drawLines(VertexArray vertexArray, int vertexCount) {
        vertexArray.bind();
        GL45.glDrawArrays(GL_LINES, 0, vertexCount);
    }

    @Override
    public void setLineWidth(float width) {
        GL45.glLineWidth(width);
    }
}
