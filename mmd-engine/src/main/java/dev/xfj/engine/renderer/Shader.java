package dev.xfj.engine.renderer;

import dev.xfj.engine.Log;
import dev.xfj.platform.opengl.OpenGLShader;
import dev.xfj.platform.opengl.OpenGLVertexArray;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL41;

import static org.lwjgl.opengl.GL41.*;

public interface Shader {
    static Shader create(String vertexSrc, String fragmentSrc) {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLShader(vertexSrc, fragmentSrc);
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }

    void bind();

    void unbind();
}
