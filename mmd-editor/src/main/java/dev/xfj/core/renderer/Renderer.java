package dev.xfj.core.renderer;

public class Renderer {
    public static void beginScene() {

    }

    public static void endScene() {

    }

    public static void submit(VertexArray vertexArray) {
        vertexArray.bind();
        RenderCommand.drawIndexed(vertexArray);
    }


    public static RendererAPIBase.API getAPI() {
        return RendererAPIBase.getAPI();
    }
}
