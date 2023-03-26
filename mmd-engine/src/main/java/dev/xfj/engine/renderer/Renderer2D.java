package dev.xfj.engine.renderer;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.buffer.BufferElement;
import dev.xfj.engine.renderer.buffer.BufferLayout;
import dev.xfj.engine.renderer.buffer.IndexBuffer;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import dev.xfj.engine.renderer.shader.Shader;
import dev.xfj.platform.opengl.OpenGLShader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.file.Path;

public class Renderer2D {
    private static Renderer2DStorage renderer2DStorage;

    public static void init() {
        renderer2DStorage = new Renderer2DStorage();
        renderer2DStorage.quadVertexArray = VertexArray.create();

        float[] squareVertices = {
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f,
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.0f, 1.0f, 1.0f,
                -0.5f, 0.5f, 0.0f, 0.0f, 1.0f
        };
        VertexBuffer squareVB = VertexBuffer.create(squareVertices);
        squareVB.setLayout(new BufferLayout(new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position"), new BufferElement(BufferElement.ShaderDataType.Float2, "a_TexCoord")));
        renderer2DStorage.quadVertexArray.addVertexBuffer(squareVB);

        int[] squareIndices = {0, 1, 2, 2, 3, 0};
        IndexBuffer squareIB = IndexBuffer.create(squareIndices, squareIndices.length);
        renderer2DStorage.quadVertexArray.setIndexBuffer(squareIB);
        try {
            renderer2DStorage.flatColorShader = Shader.create(Path.of("assets/shaders/FlatColor.glsl"));
            renderer2DStorage.textureShader = Shader.create(Path.of("assets/shaders/Texture.glsl"));
            renderer2DStorage.textureShader.bind();
            renderer2DStorage.textureShader.setInt("u_Texture", 0);
        } catch (IOException e) {
            Log.error(e.toString());
        }
    }

    public static void shutdown() {
        renderer2DStorage = null;
    }

    public static void beginScene(OrthographicCamera camera) {
        renderer2DStorage.flatColorShader.bind();
        renderer2DStorage.flatColorShader.setMat4("u_ViewProjection", camera.getViewProjectionMatrix());

        renderer2DStorage.textureShader.bind();
        renderer2DStorage.textureShader.setMat4("u_ViewProjection", camera.getViewProjectionMatrix());
    }

    public static void endScene() {

    }

    public static void drawQuad(Vector2f position, Vector2f size, Vector4f color) {
        drawQuad(new Vector3f(position.x, position.y, 0.0f), size, color);
    }

    public static void drawQuad(Vector3f position, Vector2f size, Vector4f color) {
        renderer2DStorage.flatColorShader.bind();
        renderer2DStorage.flatColorShader.setFloat4("u_Color", color);

        Matrix4f transform = new Matrix4f().translate(position.x, position.y, 0.0f).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        renderer2DStorage.flatColorShader.setMat4("u_Transform", transform);

        renderer2DStorage.quadVertexArray.bind();
        RenderCommand.drawIndexed(renderer2DStorage.quadVertexArray);
    }

    public static void drawQuad(Vector2f position, Vector2f size, Texture2D texture) {
        drawQuad(new Vector3f(position.x, position.y, 0.0f), size, texture);
    }

    public static void drawQuad(Vector3f position, Vector2f size, Texture2D texture) {
        renderer2DStorage.textureShader.bind();

        Matrix4f transform = new Matrix4f().translate(position.x, position.y, 0.0f).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        renderer2DStorage.flatColorShader.setMat4("u_Transform", transform);

        texture.bind();

        renderer2DStorage.quadVertexArray.bind();
        RenderCommand.drawIndexed(renderer2DStorage.quadVertexArray);
    }
}
