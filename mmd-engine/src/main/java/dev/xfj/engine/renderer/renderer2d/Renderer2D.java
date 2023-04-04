package dev.xfj.engine.renderer.renderer2d;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.OrthographicCamera;
import dev.xfj.engine.renderer.RenderCommand;
import dev.xfj.engine.renderer.Texture2D;
import dev.xfj.engine.renderer.VertexArray;
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
import java.util.ArrayList;
import java.util.stream.IntStream;

public class Renderer2D {
    private static Renderer2DData renderer2DData = new Renderer2DData();

    public static void init() {
        renderer2DData.quadVertexArray = VertexArray.create();

        renderer2DData.quadVertexBuffer = VertexBuffer.create(renderer2DData.maxVertices * (9 * Float.BYTES));
        renderer2DData.quadVertexBuffer.setLayout(new BufferLayout(
                new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position"),
                new BufferElement(BufferElement.ShaderDataType.Float4, "a_Color"),
                new BufferElement(BufferElement.ShaderDataType.Float2, "a_TexCoord"),
                new BufferElement(BufferElement.ShaderDataType.Float, "a_TexIndex"),
                new BufferElement(BufferElement.ShaderDataType.Float, "a_TilingFactor")));

        renderer2DData.quadVertexArray.addVertexBuffer(renderer2DData.quadVertexBuffer);

        int[] quadIndices = new int[renderer2DData.maxIndices];
        int offset = 0;
        for (int i = 0; i < renderer2DData.maxIndices; i += 6) {
            quadIndices[i + 0] = offset + 0;
            quadIndices[i + 1] = offset + 1;
            quadIndices[i + 2] = offset + 2;

            quadIndices[i + 3] = offset + 2;
            quadIndices[i + 4] = offset + 3;
            quadIndices[i + 5] = offset + 0;

            offset += 4;
        }

        IndexBuffer quadIB = IndexBuffer.create(quadIndices, renderer2DData.maxIndices);
        renderer2DData.quadVertexArray.setIndexBuffer(quadIB);

        try {
            renderer2DData.whiteTexture = Texture2D.create(1, 1);
            int whiteTextureData = 0xffffffff;
            ByteBuffer data = ByteBuffer.allocateDirect(Integer.BYTES).order(ByteOrder.nativeOrder());
            data.asIntBuffer().put(whiteTextureData);
            renderer2DData.whiteTexture.setData(data, Integer.BYTES);

            int[] samplers = new int[Renderer2DData.maxTextureSlots];

            for (int i = 0; i < Renderer2DData.maxTextureSlots; i++) {
                samplers[i] = i;
            }

            renderer2DData.textureShader = Shader.create(Path.of("assets/shaders/Texture.glsl"));
            renderer2DData.textureShader.bind();
            renderer2DData.textureShader.setIntArray("u_Textures", samplers);

            renderer2DData.textureSlots[0] = renderer2DData.whiteTexture;
        } catch (IOException e) {
            Log.error(e.toString());
        }
    }

    public static void shutdown() {
        renderer2DData = null;
    }

    public static void beginScene(OrthographicCamera camera) {
        renderer2DData.textureShader.bind();
        renderer2DData.textureShader.setMat4("u_ViewProjection", camera.getViewProjectionMatrix());

        renderer2DData.quadIndexCount = 0;
        renderer2DData.quadVertexBufferPtr = 0;
        renderer2DData.quadVertexBufferBase = new ArrayList<>(renderer2DData.maxIndices);
        renderer2DData.textureSlotIndex = 1;
    }

    public static void endScene() {
        float[] temp = new float[renderer2DData.quadVertexBufferPtr * QuadVertex.getQuadVertexSize()];
        ArrayList<Float> list = new ArrayList<>();
        //Surely there must be a better way than to do this
        for (int i = 0; i < renderer2DData.quadVertexBufferPtr; i++) {
            list.addAll(renderer2DData.quadVertexBufferBase.get(i).toList());
        }

        IntStream.range(0, list.size()).forEach(i -> temp[i] = list.get(i));
        renderer2DData.quadVertexBuffer.setData(temp);
        flush();
    }

    public static void flush() {
        for (int i = 0; i < renderer2DData.textureSlotIndex; i++) {
            renderer2DData.textureSlots[i].bind(i);
        }
        RenderCommand.drawIndexed(renderer2DData.quadVertexArray, renderer2DData.quadIndexCount);
    }

    public static void drawQuad(Vector2f position, Vector2f size, Vector4f color) {
        drawQuad(new Vector3f(position.x, position.y, 0.0f), size, color);
    }

    public static void drawQuad(Vector3f position, Vector2f size, Vector4f color) {
        float texIndex = 0.0f;
        float tilingFactor = 1.0f;

        renderer2DData.quadVertexBufferBase.add(new QuadVertex(position, color, new Vector2f(0.0f, 0.0f), texIndex, tilingFactor));
        renderer2DData.quadVertexBufferPtr++;

        renderer2DData.quadVertexBufferBase.add(new QuadVertex(new Vector3f(position.x + size.x, position.y, 0.0f), color, new Vector2f(1.0f, 0.0f), texIndex, tilingFactor));
        renderer2DData.quadVertexBufferPtr++;

        renderer2DData.quadVertexBufferBase.add(new QuadVertex(new Vector3f(position.x + size.x, position.y + size.y, 0.0f), color, new Vector2f(1.0f, 1.0f), texIndex, tilingFactor));
        renderer2DData.quadVertexBufferPtr++;

        renderer2DData.quadVertexBufferBase.add(new QuadVertex(new Vector3f(position.x, position.y + size.y, 0.0f), color, new Vector2f(0.0f, 1.0f), texIndex, tilingFactor));
        renderer2DData.quadVertexBufferPtr++;

        renderer2DData.quadIndexCount += 6;

        /*renderer2DData.textureShader.setFloat4("u_Color", color);
        renderer2DData.whiteTexture.bind();

        Matrix4f transform = new Matrix4f().translate(position.x, position.y, position.z).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        renderer2DData.textureShader.setMat4("u_Transform", transform);

        renderer2DData.quadVertexArray.bind();
        RenderCommand.drawIndexed(renderer2DData.quadVertexArray);*/
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
        Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

        float textureIndex = 0.0f;
        for (int i = 1; i < renderer2DData.textureSlotIndex; i++) {
            if (renderer2DData.textureSlots[i].equals(texture)) {
                textureIndex = (float) i;
                break;
            }
        }

        if (textureIndex == 0.0f) {
            textureIndex = (float) renderer2DData.textureSlotIndex;
            renderer2DData.textureSlots[renderer2DData.textureSlotIndex] = texture;
            renderer2DData.textureSlotIndex++;
        }

        renderer2DData.quadVertexBufferBase.add(new QuadVertex(position, color, new Vector2f(0.0f, 0.0f), textureIndex, tilingFactor));
        renderer2DData.quadVertexBufferPtr++;

        renderer2DData.quadVertexBufferBase.add(new QuadVertex(new Vector3f(position.x + size.x, position.y, 0.0f), color, new Vector2f(1.0f, 0.0f), textureIndex, tilingFactor));
        renderer2DData.quadVertexBufferPtr++;

        renderer2DData.quadVertexBufferBase.add(new QuadVertex(new Vector3f(position.x + size.x, position.y + size.y, 0.0f), color, new Vector2f(1.0f, 1.0f), textureIndex, tilingFactor));
        renderer2DData.quadVertexBufferPtr++;

        renderer2DData.quadVertexBufferBase.add(new QuadVertex(new Vector3f(position.x, position.y + size.y, 0.0f), color, new Vector2f(0.0f, 1.0f), textureIndex, tilingFactor));
        renderer2DData.quadVertexBufferPtr++;

        renderer2DData.quadIndexCount += 6;

        /*renderer2DData.textureShader.setFloat4("u_Color", tintColor);
        renderer2DData.textureShader.setFloat("u_TilingFactor", tilingFactor);
        texture.bind();

        Matrix4f transform = new Matrix4f().translate(position.x, position.y, position.z).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        renderer2DData.textureShader.setMat4("u_Transform", transform);

        renderer2DData.quadVertexArray.bind();
        RenderCommand.drawIndexed(renderer2DData.quadVertexArray);*/
    }

    public static void drawRotatedQuad(Vector2f position, Vector2f size, float rotation, Vector4f color) {
        drawRotatedQuad(new Vector3f(position.x, position.y, 0.0f), size, rotation, color);
    }

    public static void drawRotatedQuad(Vector3f position, Vector2f size, float rotation, Vector4f color) {
        renderer2DData.textureShader.setFloat4("u_Color", color);
        renderer2DData.textureShader.setFloat("u_TilingFactor", 1.0f);
        renderer2DData.whiteTexture.bind();

        Matrix4f transform = new Matrix4f().translate(position.x, position.y, position.z).mul(new Matrix4f().rotate(rotation, new Vector3f(0.0f, 0.0f, 1.0f))).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        renderer2DData.textureShader.setMat4("u_Transform", transform);

        renderer2DData.quadVertexArray.bind();
        RenderCommand.drawIndexed(renderer2DData.quadVertexArray);
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
        renderer2DData.textureShader.setFloat4("u_Color", tintColor);
        renderer2DData.textureShader.setFloat("u_TilingFactor", tilingFactor);
        texture.bind();

        Matrix4f transform = new Matrix4f().translate(position.x, position.y, position.z).mul(new Matrix4f().rotate(rotation, new Vector3f(0.0f, 0.0f, 1.0f))).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        renderer2DData.textureShader.setMat4("u_Transform", transform);

        renderer2DData.quadVertexArray.bind();
        RenderCommand.drawIndexed(renderer2DData.quadVertexArray);
    }
}
