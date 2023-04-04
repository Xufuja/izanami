package dev.xfj.engine.renderer.shader;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.Renderer;
import dev.xfj.platform.opengl.OpenGLShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.file.Path;

public interface Shader {
    static Shader create(Path filePath) throws IOException {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLShader(filePath);
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }

    static Shader create(String name, String vertexSrc, String fragmentSrc) {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLShader(name, vertexSrc, fragmentSrc);
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }

    void bind();

    void unbind();

    void setInt(String name, int value);
    void setIntArray(String name, int[] values);

    void setFloat(String name, float value);

    void setFloat3(String name, Vector3f value);

    void setFloat4(String name, Vector4f value);

    void setMat4(String name, Matrix4f value);

    String getName();
}
