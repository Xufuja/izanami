package dev.xfj.engine.renderer;

import dev.xfj.engine.core.Log;
import dev.xfj.platform.opengl.OpenGLTexture2D;

import java.nio.file.Path;

public abstract class Texture2D implements Texture {
    public static Texture2D create(int width, int height) {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLTexture2D(width, height);
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }
    public static Texture2D create(Path path) {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLTexture2D(path);
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }
}
