package dev.xfj.core.renderer;

public class Renderer {
    private static final RendererAPI rendererAPI;

    static {
        rendererAPI = RendererAPI.OpenGL;
    }

    public enum RendererAPI {
        None,
        OpenGL
    }

    public static RendererAPI getAPI() {
        return rendererAPI;
    }
}
