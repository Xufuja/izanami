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
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f
        };
        VertexBuffer squareVB = VertexBuffer.create(squareVertices);
        squareVB.setLayout(new BufferLayout(new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position")));
        renderer2DStorage.quadVertexArray.addVertexBuffer(squareVB);

        int[] squareIndices = {0, 1, 2, 2, 3, 0};
        IndexBuffer squareIB = IndexBuffer.create(squareIndices, squareIndices.length);
        renderer2DStorage.quadVertexArray.setIndexBuffer(squareIB);
        try {
            renderer2DStorage.flatColorShader = Shader.create(Path.of("assets/shaders/FlatColor.glsl"));
        } catch (IOException e) {
            Log.error(e.toString());
        }
    }

    public static void shutdown() {
        renderer2DStorage = null;
    }

    public static void beginScene(OrthographicCamera camera) {
        renderer2DStorage.flatColorShader.bind();
        ((OpenGLShader) renderer2DStorage.flatColorShader).uploadUniformMat4("u_ViewProjection", camera.getViewProjectionMatrix());
        ((OpenGLShader) renderer2DStorage.flatColorShader).uploadUniformMat4("u_Transform", new Matrix4f().identity());
    }

    public static void endScene() {

    }

    public static void drawQuad(Vector2f position, Vector2f size, Vector4f color) {
        drawQuad(new Vector3f(position.x, position.y, 0.0f), size, color);
    }

    public static void drawQuad(Vector3f position, Vector2f size, Vector4f color) {
        renderer2DStorage.flatColorShader.bind();
        ((OpenGLShader) renderer2DStorage.flatColorShader).uploadUniformFloat4("u_Color", color);

        renderer2DStorage.quadVertexArray.bind();
        RenderCommand.drawIndexed(renderer2DStorage.quadVertexArray);
    }
}
