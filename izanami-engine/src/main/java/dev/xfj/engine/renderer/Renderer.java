package dev.xfj.engine.renderer;

import dev.xfj.engine.renderer.renderer2d.Renderer2D;
import dev.xfj.engine.renderer.shader.Shader;
import org.joml.Matrix4f;

public class Renderer {
    private static SceneData sceneData;

    static {
        Renderer.sceneData = new SceneData();
    }

    public static void init() {
        RenderCommand.init();
        Renderer2D.init();
    }

    public static void beginScene(OrthographicCamera camera) {
        sceneData.viewProjectionMatrix = camera.getViewProjectionMatrix();
    }

    public static void shutdown() {
        Renderer2D.shutdown();
    }

    public static void onWindowResize(int width, int height) {
        RenderCommand.setViewport(0, 0, width, height);
    }

    public static void endScene() {

    }

    public static void submit(Shader shader, VertexArray vertexArray) {
        submit(shader, vertexArray, new Matrix4f().identity());
    }

    public static void submit(Shader shader, VertexArray vertexArray, Matrix4f transform) {
        shader.bind();

        shader.setMat4("u_ViewProjection", sceneData.viewProjectionMatrix);
        shader.setMat4("u_Transform", transform);

        vertexArray.bind();
        RenderCommand.drawIndexed(vertexArray);

    }

    public static RendererAPIBase.API getAPI() {
        return RendererAPIBase.getAPI();
    }
}
