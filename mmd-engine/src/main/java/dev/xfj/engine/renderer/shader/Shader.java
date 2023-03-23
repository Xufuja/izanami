package dev.xfj.engine.renderer.shader;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.Renderer;
import dev.xfj.platform.opengl.OpenGLShader;

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

    String getName();
}
