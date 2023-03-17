package dev.xfj.engine.renderer;

import dev.xfj.engine.Log;
import dev.xfj.platform.opengl.OpenGLTexture2D;

public abstract class Texture2D implements Texture {
    public static Texture2D create(String path) {
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
