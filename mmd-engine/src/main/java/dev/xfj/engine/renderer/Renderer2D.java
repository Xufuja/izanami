package dev.xfj.engine.renderer;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.buffer.BufferElement;
import dev.xfj.engine.renderer.buffer.BufferLayout;
import dev.xfj.engine.renderer.buffer.IndexBuffer;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import dev.xfj.engine.renderer.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
            renderer2DStorage.whiteTexture = Texture2D.create(1, 1);
            int whiteTextureData = 0xffffffff;
            ByteBuffer data = ByteBuffer.allocateDirect(Integer.BYTES).order(ByteOrder.nativeOrder());
            data.asIntBuffer().put(whiteTextureData);
            renderer2DStorage.whiteTexture.setData(data, Integer.BYTES);

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
        renderer2DStorage.textureShader.bind();
        renderer2DStorage.textureShader.setMat4("u_ViewProjection", camera.getViewProjectionMatrix());
    }

    public static void endScene() {

    }

    public static void drawQuad(Vector2f position, Vector2f size, Vector4f color) {
        drawQuad(new Vector3f(position.x, position.y, 0.0f), size, color);
    }

    public static void drawQuad(Vector3f position, Vector2f size, Vector4f color) {
        renderer2DStorage.textureShader.setFloat4("u_Color", color);
        renderer2DStorage.whiteTexture.bind();

        Matrix4f transform = new Matrix4f().translate(position.x, position.y, 0.0f).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        renderer2DStorage.textureShader.setMat4("u_Transform", transform);

        renderer2DStorage.quadVertexArray.bind();
        RenderCommand.drawIndexed(renderer2DStorage.quadVertexArray);
    }

    public static void drawQuad(Vector2f position, Vector2f size, Texture2D texture) {
        drawQuad(position, size, texture, 1.0f, new Vector4f(1.0f));
    }

    public static void drawQuad(Vector2f position, Vector2f size, Texture2D texture, float tilingFactor) {
        drawQuad(position, size, texture, tilingFactor, new Vector4f(1.0f));
    }

    public static void drawQuad(Vector2f position, Vector2f size, Texture2D texture, float tilingFactor, Vector4f tintColor) {
        drawQuad(new Vector3f(position.x, position.y, 0.0f), size, texture, tilingFactor, tintColor);
    }

    public static void drawQuad(Vector3f position, Vector2f size, Texture2D texture) {
        drawQuad(position, size, texture, 1.0f);
    }

    public static void drawQuad(Vector3f position, Vector2f size, Texture2D texture, float tilingFactor) {
        drawQuad(position, size, texture, tilingFactor, new Vector4f(1.0f));
    }

    public static void drawQuad(Vector3f position, Vector2f size, Texture2D texture, float tilingFactor, Vector4f tintColor) {
        renderer2DStorage.textureShader.setFloat4("u_Color", tintColor);
        renderer2DStorage.textureShader.setFloat("u_TilingFactor", tilingFactor);
        texture.bind();

        Matrix4f transform = new Matrix4f().translate(position.x, position.y, 0.0f).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        renderer2DStorage.textureShader.setMat4("u_Transform", transform);

        renderer2DStorage.quadVertexArray.bind();
        RenderCommand.drawIndexed(renderer2DStorage.quadVertexArray);
    }

    public static void drawRotatedQuad(Vector2f position, Vector2f size, float rotation, Vector4f color) {
        drawRotatedQuad(new Vector3f(position.x, position.y, 0.0f), size, rotation, color);
    }

    public static void drawRotatedQuad(Vector3f position, Vector2f size, float rotation, Vector4f color) {
        renderer2DStorage.textureShader.setFloat4("u_Color", color);
        renderer2DStorage.textureShader.setFloat("u_TilingFactor", 1.0f);
        renderer2DStorage.whiteTexture.bind();

        Matrix4f transform = new Matrix4f().translate(position.x, position.y, 0.0f).mul(new Matrix4f().rotate(rotation, new Vector3f(0.0f, 0.0f, 1.0f))).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        renderer2DStorage.textureShader.setMat4("u_Transform", transform);

        renderer2DStorage.quadVertexArray.bind();
        RenderCommand.drawIndexed(renderer2DStorage.quadVertexArray);
    }

    public static void drawRotatedQuad(Vector2f position, Vector2f size, float rotation, Texture2D texture) {
        drawRotatedQuad(position, size, rotation, texture, 1.0f);
    }

    public static void drawRotatedQuad(Vector2f position, Vector2f size, float rotation, Texture2D texture, float tilingFactor) {
        drawRotatedQuad(position, size, rotation, texture, tilingFactor, new Vector4f(1.0f));
    }

    public static void drawRotatedQuad(Vector2f position, Vector2f size, float rotation, Texture2D texture, float tilingFactor, Vector4f tintColor) {
        drawRotatedQuad(new Vector3f(position.x, position.y, 0.0f), size, rotation, texture, tilingFactor, tintColor);
    }

    public static void drawRotatedQuad(Vector3f position, Vector2f size, float rotation, Texture2D texture) {
        drawRotatedQuad(position, size, rotation, texture, 1.0f);
    }

    public static void drawRotatedQuad(Vector3f position, Vector2f size, float rotation, Texture2D texture, float tilingFactor) {
        drawRotatedQuad(position, size, rotation, texture, tilingFactor, new Vector4f(1.0f));
    }
    public static void drawRotatedQuad(Vector3f position, Vector2f size, float rotation, Texture2D texture, float tilingFactor, Vector4f tintColor) {
        renderer2DStorage.textureShader.setFloat4("u_Color", tintColor);
        renderer2DStorage.textureShader.setFloat("u_TilingFactor", tilingFactor);
        texture.bind();

        Matrix4f transform = new Matrix4f().translate(position.x, position.y, 0.0f).mul(new Matrix4f().rotate(rotation, new Vector3f(0.0f, 0.0f, 1.0f))).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        renderer2DStorage.textureShader.setMat4("u_Transform", transform);

        renderer2DStorage.quadVertexArray.bind();
        RenderCommand.drawIndexed(renderer2DStorage.quadVertexArray);
    }
}
