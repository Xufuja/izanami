package dev.xfj.engine.renderer;

import dev.xfj.platform.opengl.OpenGLShader;
import org.joml.Matrix4f;

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
        submit(shader, vertexArray, new Matrix4f().identity());
    }

    public static void submit(Shader shader, VertexArray vertexArray, Matrix4f transform) {
        shader.bind();
        if (shader instanceof OpenGLShader) {
            ((OpenGLShader) shader).uploadUniformMat4("u_ViewProjection", sceneData.viewProjectionMatrix);
            ((OpenGLShader) shader).uploadUniformMat4("u_Transform", transform);
        }

        vertexArray.bind();
        RenderCommand.drawIndexed(vertexArray);

    }

    public static RendererAPIBase.API getAPI() {
        return RendererAPIBase.getAPI();
    }
}
