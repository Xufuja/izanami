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
    private static Renderer2DData data = new Renderer2DData();

    public static void init() {
        data.quadVertexArray = VertexArray.create();

        data.quadVertexBuffer = VertexBuffer.create(Renderer2DData.maxVertices * (9 * Float.BYTES));
        data.quadVertexBuffer.setLayout(new BufferLayout(
                new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position"),
                new BufferElement(BufferElement.ShaderDataType.Float4, "a_Color"),
                new BufferElement(BufferElement.ShaderDataType.Float2, "a_TexCoord"),
                new BufferElement(BufferElement.ShaderDataType.Float, "a_TexIndex"),
                new BufferElement(BufferElement.ShaderDataType.Float, "a_TilingFactor")));

        data.quadVertexArray.addVertexBuffer(data.quadVertexBuffer);

        data.quadVertexBufferBase = new ArrayList<>();
        while (data.quadVertexBufferBase.size() < Renderer2DData.maxIndices) {
            data.quadVertexBufferBase.add(new QuadVertex());
        }

        int[] quadIndices = new int[Renderer2DData.maxIndices];
        int offset = 0;
        for (int i = 0; i < Renderer2DData.maxIndices; i += 6) {
            quadIndices[i + 0] = offset + 0;
            quadIndices[i + 1] = offset + 1;
            quadIndices[i + 2] = offset + 2;

            quadIndices[i + 3] = offset + 2;
            quadIndices[i + 4] = offset + 3;
            quadIndices[i + 5] = offset + 0;

            offset += 4;
        }

        IndexBuffer quadIB = IndexBuffer.create(quadIndices, Renderer2DData.maxIndices);
        data.quadVertexArray.setIndexBuffer(quadIB);

        try {
            data.whiteTexture = Texture2D.create(1, 1);
            int whiteTextureData = 0xffffffff;
            ByteBuffer data = ByteBuffer.allocateDirect(Integer.BYTES).order(ByteOrder.nativeOrder());
            data.asIntBuffer().put(whiteTextureData);
            Renderer2D.data.whiteTexture.setData(data, Integer.BYTES);

            int[] samplers = new int[Renderer2DData.maxTextureSlots];

            for (int i = 0; i < Renderer2DData.maxTextureSlots; i++) {
                samplers[i] = i;
            }

            Renderer2D.data.textureShader = Shader.create(Path.of("assets/shaders/Texture.glsl"));
            Renderer2D.data.textureShader.bind();
            Renderer2D.data.textureShader.setIntArray("u_Textures", samplers);

            Renderer2D.data.textureSlots[0] = Renderer2D.data.whiteTexture;

            Renderer2D.data.quadVertexPositions[0] = new Vector4f(-0.5f, -0.5f, 0.0f, 1.0f);
            Renderer2D.data.quadVertexPositions[1] = new Vector4f(0.5f, -0.5f, 0.0f, 1.0f);
            Renderer2D.data.quadVertexPositions[2] = new Vector4f(0.5f, 0.5f, 0.0f, 1.0f);
            Renderer2D.data.quadVertexPositions[3] = new Vector4f(-0.5f, 0.5f, 0.0f, 1.0f);
        } catch (IOException e) {
            Log.error(e.toString());
        }
    }

    public static void shutdown() {
        data = null;
    }

    public static void beginScene(OrthographicCamera camera) {
        data.textureShader.bind();
        data.textureShader.setMat4("u_ViewProjection", camera.getViewProjectionMatrix());

        data.quadIndexCount = 0;
        data.quadVertexBufferPtr = 0;
        data.textureSlotIndex = 1;
    }

    public static void endScene() {
        float[] temp = new float[data.quadVertexBufferPtr * QuadVertex.getQuadVertexSize()];
        ArrayList<Float> list = new ArrayList<>();
        //Surely there must be a better way than to do this
        for (int i = 0; i < data.quadVertexBufferPtr; i++) {
            list.addAll(data.quadVertexBufferBase.get(i).toList());
        }

        IntStream.range(0, list.size()).forEach(i -> temp[i] = list.get(i));
        data.quadVertexBuffer.setData(temp);
        flush();
    }

    public static void flush() {
        if (data.quadIndexCount == 0) {
            return;
        }
        for (int i = 0; i < data.textureSlotIndex; i++) {
            data.textureSlots[i].bind(i);
        }
        RenderCommand.drawIndexed(data.quadVertexArray, data.quadIndexCount);
        data.stats.drawCalls++;
    }

    private static void flushAndReset() {
        endScene();

        data.quadIndexCount = 0;
        data.quadVertexBufferPtr = 0;
        data.textureSlotIndex = 1;
    }

    public static void drawQuad(Vector2f position, Vector2f size, Vector4f color) {
        drawQuad(new Vector3f(position.x, position.y, 0.0f), size, color);
    }

    public static void drawQuad(Vector3f position, Vector2f size, Vector4f color) {
        if (data.quadIndexCount >= Renderer2DData.maxIndices) {
            flushAndReset();
        }

        int quadVertexCount = 4;
        float textureIndex = 0.0f;
        Vector2f[] textureCoords = new Vector2f[]{new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 0.0f), new Vector2f(1.0f, 1.0f), new Vector2f(0.0f, 1.0f)};
        float tilingFactor = 1.0f;

        for (int i = 0; i < quadVertexCount; i++) {
            data.quadVertexBufferBase.get(data.quadVertexBufferPtr).setQuadVertex(transformPosition(position.x, position.y, position.z, size.x, size.y, i), color, textureCoords[i], textureIndex, tilingFactor);
            data.quadVertexBufferPtr++;
        }

        data.quadIndexCount += 6;
        data.stats.quadCount++;
    }

    public static Vector3f transformPosition(float posX, float posY, float posZ, float sizeX, float sizeY, int quadVertexPosition) {
        Matrix4f transform = new Matrix4f().translate(posX, posY, posZ).mul(new Matrix4f().scale(sizeX, sizeY, 1.0f));
        Vector4f transformedPosition = transform.transform(data.quadVertexPositions[quadVertexPosition], new Vector4f());
        return new Vector3f(transformedPosition.x, transformedPosition.y, transformedPosition.z);
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
        if (data.quadIndexCount >= Renderer2DData.maxIndices) {
            flushAndReset();
        }

        int quadVertexCount = 4;
        float textureIndex = 0.0f;
        Vector2f[] textureCoords = new Vector2f[]{new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 0.0f), new Vector2f(1.0f, 1.0f), new Vector2f(0.0f, 1.0f)};

        for (int i = 1; i < data.textureSlotIndex; i++) {
            if (data.textureSlots[i].equals(texture)) {
                textureIndex = (float) i;
                break;
            }
        }

        if (textureIndex == 0.0f) {
            textureIndex = (float) data.textureSlotIndex;
            data.textureSlots[data.textureSlotIndex] = texture;
            data.textureSlotIndex++;
        }

        for (int i = 0; i < quadVertexCount; i++) {
            data.quadVertexBufferBase.get(data.quadVertexBufferPtr).setQuadVertex(transformPosition(position.x, position.y, position.z, size.x, size.y, i), tintColor, textureCoords[i], textureIndex, tilingFactor);
            data.quadVertexBufferPtr++;
        }

        data.quadIndexCount += 6;
        data.stats.quadCount++;
    }

    public static void drawRotatedQuad(Vector2f position, Vector2f size, float rotation, Vector4f color) {
        drawRotatedQuad(new Vector3f(position.x, position.y, 0.0f), size, rotation, color);
    }

    public static void drawRotatedQuad(Vector3f position, Vector2f size, float rotation, Vector4f color) {
        if (data.quadIndexCount >= Renderer2DData.maxIndices) {
            flushAndReset();
        }

        int quadVertexCount = 4;
        float textureIndex = 0.0f;
        Vector2f[] textureCoords = new Vector2f[]{new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 0.0f), new Vector2f(1.0f, 1.0f), new Vector2f(0.0f, 1.0f)};
        float tilingFactor = 1.0f;

        for (int i = 0; i < quadVertexCount; i++) {
            data.quadVertexBufferBase.get(data.quadVertexBufferPtr).setQuadVertex(transformRotatedPosition(position.x, position.y, position.z, rotation, size.x, size.y, i), color, textureCoords[i], textureIndex, tilingFactor);
            data.quadVertexBufferPtr++;
        }

        data.quadIndexCount += 6;
        data.stats.quadCount++;
    }

    public static Vector3f transformRotatedPosition(float posX, float posY, float posZ, float rotation, float sizeX, float sizeY, int quadVertexPosition) {
        Matrix4f transform = new Matrix4f().translate(posX, posY, posZ).mul(new Matrix4f().rotate((float) Math.toRadians(rotation), new Vector3f(0.0f, 0.0f, 1.0f))).mul(new Matrix4f().scale(sizeX, sizeY, 1.0f));
        Vector4f transformedPosition = transform.transform(data.quadVertexPositions[quadVertexPosition], new Vector4f());
        return new Vector3f(transformedPosition.x, transformedPosition.y, transformedPosition.z);
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
        if (data.quadIndexCount >= Renderer2DData.maxIndices) {
            flushAndReset();
        }

        int quadVertexCount = 4;
        float textureIndex = 0.0f;
        Vector2f[] textureCoords = new Vector2f[]{new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 0.0f), new Vector2f(1.0f, 1.0f), new Vector2f(0.0f, 1.0f)};

        for (int i = 1; i < data.textureSlotIndex; i++) {
            if (data.textureSlots[i].equals(texture)) {
                textureIndex = (float) i;
                break;
            }
        }

        if (textureIndex == 0.0f) {
            textureIndex = (float) data.textureSlotIndex;
            data.textureSlots[data.textureSlotIndex] = texture;
            data.textureSlotIndex++;
        }

        for (int i = 0; i < quadVertexCount; i++) {
            data.quadVertexBufferBase.get(data.quadVertexBufferPtr).setQuadVertex(transformRotatedPosition(position.x, position.y, position.z, rotation, size.x, size.y, i), tintColor, textureCoords[i], textureIndex, tilingFactor);
            data.quadVertexBufferPtr++;
        }

        data.quadIndexCount += 6;
        data.stats.quadCount++;
    }

    public static void resetStats() {
        data.stats = new Statistics();
    }

    public static Statistics getStats() {
        return data.stats;
    }
}
