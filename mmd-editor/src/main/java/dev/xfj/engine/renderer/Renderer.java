package dev.xfj.engine.renderer;

public class Renderer {
    private static SceneData sceneData;

    static {
        Renderer.sceneData = new SceneData();
    }

    public static void beginScene(OrthographicCamera camera) {
        sceneData.viewProjectionMatrix = camera.getViewProjectionMatrix();
    }

    public static void endScene() {

    }

    public static void submit(Shader shader, VertexArray vertexArray) {
        shader.bind();
        shader.uploadUniformMat4("u_ViewProjection", sceneData.viewProjectionMatrix);
        vertexArray.bind();
        RenderCommand.drawIndexed(vertexArray);
    }


    public static RendererAPIBase.API getAPI() {
        return RendererAPIBase.getAPI();
    }
}
