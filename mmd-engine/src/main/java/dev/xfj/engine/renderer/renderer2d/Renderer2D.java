package dev.xfj.engine.renderer.renderer2d;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.*;
import dev.xfj.engine.renderer.buffer.BufferElement;
import dev.xfj.engine.renderer.buffer.BufferLayout;
import dev.xfj.engine.renderer.buffer.IndexBuffer;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import dev.xfj.engine.renderer.shader.Shader;
import dev.xfj.engine.scene.components.SpriteRendererComponent;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.util.ArrayList;

public class Renderer2D {
    private static Renderer2DData data = new Renderer2DData();

    public static void init() {
        data.quadVertexArray = VertexArray.create();

        data.quadVertexBuffer = VertexBuffer.create(Renderer2DData.maxVertices * QuadVertex.getVertexSize());
        data.quadVertexBuffer.setLayout(new BufferLayout(
                new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position"),
                new BufferElement(BufferElement.ShaderDataType.Float4, "a_Color"),
                new BufferElement(BufferElement.ShaderDataType.Float2, "a_TexCoord"),
                new BufferElement(BufferElement.ShaderDataType.Float, "a_TexIndex"),
                new BufferElement(BufferElement.ShaderDataType.Float, "a_TilingFactor"),
                new BufferElement(BufferElement.ShaderDataType.Int, "a_EntityID")));

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

        data.circleVertexArray = VertexArray.create();

        data.circleVertexBuffer = VertexBuffer.create(Renderer2DData.maxVertices * CircleVertex.getVertexSize());
        data.circleVertexBuffer.setLayout(new BufferLayout(
                new BufferElement(BufferElement.ShaderDataType.Float3, "a_WorldPosition"),
                new BufferElement(BufferElement.ShaderDataType.Float3, "a_LocalPosition"),
                new BufferElement(BufferElement.ShaderDataType.Float4, "a_Color"),
                new BufferElement(BufferElement.ShaderDataType.Float, "a_Thickness"),
                new BufferElement(BufferElement.ShaderDataType.Float, "a_Fade"),
                new BufferElement(BufferElement.ShaderDataType.Int, "a_EntityID")));

        data.circleVertexArray.addVertexBuffer(data.circleVertexBuffer);
        data.circleVertexArray.setIndexBuffer(quadIB);
        data.circleVertexBufferBase = new ArrayList<>();

        while (data.circleVertexBufferBase.size() < Renderer2DData.maxIndices) {
            data.circleVertexBufferBase.add(new CircleVertex());
        }

        data.lineVertexArray = VertexArray.create();

        data.lineVertexBuffer = VertexBuffer.create(Renderer2DData.maxVertices * LineVertex.getVertexSize());
        data.lineVertexBuffer.setLayout(new BufferLayout(
                new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position"),
                new BufferElement(BufferElement.ShaderDataType.Float4, "a_Color"),
                new BufferElement(BufferElement.ShaderDataType.Int, "a_EntityID")));

        data.lineVertexArray.addVertexBuffer(data.lineVertexBuffer);
        data.lineVertexBufferBase = new ArrayList<>();

        while (data.lineVertexBufferBase.size() < Renderer2DData.maxIndices) {
            data.lineVertexBufferBase.add(new LineVertex());
        }

        try {
            data.whiteTexture = Texture2D.create(1, 1);
            int whiteTextureData = 0xffffffff;
            //Using MemoryUtil.memAllocInt(1); does not work for some reason
            ByteBuffer data = ByteBuffer.allocateDirect(Integer.BYTES).order(ByteOrder.nativeOrder());
            data.asIntBuffer().put(whiteTextureData);
            Renderer2D.data.whiteTexture.setData(data, Integer.BYTES);

            int[] samplers = new int[Renderer2DData.maxTextureSlots];

            for (int i = 0; i < Renderer2DData.maxTextureSlots; i++) {
                samplers[i] = i;
            }

            Renderer2D.data.quadShader = Shader.create(Path.of("assets/shaders/Renderer2D_Quad.glsl"));
            Renderer2D.data.circleShader = Shader.create(Path.of("assets/shaders/Renderer2D_Circle.glsl"));
            Renderer2D.data.lineShader = Shader.create(Path.of("assets/shaders/Renderer2D_Line.glsl"));

            Renderer2D.data.textureSlots[0] = Renderer2D.data.whiteTexture;

            Renderer2D.data.quadVertexPositions[0] = new Vector4f(-0.5f, -0.5f, 0.0f, 1.0f);
            Renderer2D.data.quadVertexPositions[1] = new Vector4f(0.5f, -0.5f, 0.0f, 1.0f);
            Renderer2D.data.quadVertexPositions[2] = new Vector4f(0.5f, 0.5f, 0.0f, 1.0f);
            Renderer2D.data.quadVertexPositions[3] = new Vector4f(-0.5f, 0.5f, 0.0f, 1.0f);

            Renderer2D.data.cameraUniformBuffer = UniformBuffer.create(16 * Float.BYTES, 0);
        } catch (IOException e) {
            Log.error(e.toString());
        }
    }

    public static void shutdown() {
        data = null;
    }

    public static void beginScene(Camera camera, Matrix4f transform) {
        Renderer2D.data.cameraBuffer.viewProjection = camera.getProjection().mul(new Matrix4f(transform.get(new Matrix4f()).invert()), new Matrix4f());
        FloatBuffer data = Renderer2D.data.cameraBuffer.viewProjection.get(MemoryUtil.memAllocFloat(16));
        Renderer2D.data.cameraUniformBuffer.setData(data);

        startBatch();
    }

    public static void beginScene(EditorCamera camera) {
        Renderer2D.data.cameraBuffer.viewProjection = camera.getViewProjection();
        FloatBuffer data = Renderer2D.data.cameraBuffer.viewProjection.get(MemoryUtil.memAllocFloat(16));
        Renderer2D.data.cameraUniformBuffer.setData(data);

        startBatch();
    }

    public static void beginScene(OrthographicCamera camera) {
        Renderer2D.data.cameraBuffer.viewProjection = camera.getViewProjectionMatrix();
        FloatBuffer data = Renderer2D.data.cameraBuffer.viewProjection.get(MemoryUtil.memAllocFloat(16));
        Renderer2D.data.cameraUniformBuffer.setData(data);

        startBatch();
    }

    public static void endScene() {

        flush();
    }

    public static void startBatch() {
        data.quadIndexCount = 0;
        data.quadVertexBufferPtr = 0;

        data.circleIndexCount = 0;
        data.circleVertexBufferPtr = 0;

        data.lineVertexCount = 0;
        data.lineVertexBufferPtr = 0;

        data.textureSlotIndex = 1;
    }

    public static void flush() {
        if (data.quadIndexCount > 0) {
            ArrayList<ByteBuffer> quadVertexBuffers = new ArrayList<>();

            for (int i = 0; i < data.quadVertexBufferPtr; i++) {
                quadVertexBuffers.add(data.quadVertexBufferBase.get(i).getAsBuffer());
            }

            data.quadVertexBuffer.setData(quadVertexBuffers, QuadVertex.getFloatArrayCount(), QuadVertex.getIntArrayCount());

            for (int i = 0; i < data.textureSlotIndex; i++) {
                data.textureSlots[i].bind(i);
            }

            Renderer2D.data.quadShader.bind();
            RenderCommand.drawIndexed(data.quadVertexArray, data.quadIndexCount);
            data.stats.drawCalls++;
        }

        if (data.circleIndexCount > 0) {
            ArrayList<ByteBuffer> circleVertexBuffers = new ArrayList<>();

            for (int i = 0; i < data.circleVertexBufferPtr; i++) {
                circleVertexBuffers.add(data.circleVertexBufferBase.get(i).getAsBuffer());
            }

            data.circleVertexBuffer.setData(circleVertexBuffers, CircleVertex.getFloatArrayCount(), CircleVertex.getIntArrayCount());

            Renderer2D.data.circleShader.bind();
            RenderCommand.drawIndexed(data.circleVertexArray, data.circleIndexCount);
            data.stats.drawCalls++;
        }

        if (data.lineVertexCount > 0) {
            ArrayList<ByteBuffer> lineVertexBuffers = new ArrayList<>();

            for (int i = 0; i < data.lineVertexBufferPtr; i++) {
                lineVertexBuffers.add(data.lineVertexBufferBase.get(i).getAsBuffer());
            }

            data.lineVertexBuffer.setData(lineVertexBuffers, LineVertex.getFloatArrayCount(), LineVertex.getIntArrayCount());

            Renderer2D.data.lineShader.bind();
            RenderCommand.setLineWidth(data.lineWidth);
            RenderCommand.drawLines(data.lineVertexArray, data.lineVertexCount);
            data.stats.drawCalls++;
        }
    }

    private static void nextBatch() {
        flush();
        startBatch();
    }

    public static void drawLine(Vector3f p0, Vector3f p1, Vector4f color) {
        drawLine(p0, p1, color, -1);
    }

    public static void drawLine(Vector3f p0, Vector3f p1, Vector4f color, int entityId) {
        data.lineVertexBufferBase.get(data.lineVertexBufferPtr).setLineVertex(p0, color, entityId);
        data.lineVertexBufferPtr++;

        data.lineVertexBufferBase.get(data.lineVertexBufferPtr).setLineVertex(p1, color, entityId);
        data.lineVertexBufferPtr++;

        data.lineVertexCount += 2;
    }

    public static void drawRect(Vector3f position, Vector2f size, Vector4f color) {
        drawRect(position, size, color, -1);
    }

    public static void drawRect(Vector3f position, Vector2f size, Vector4f color, int entityId) {
        Vector3f p0 = new Vector3f(position.x - size.x * 0.5f, position.y - size.y * 0.5f, position.z);
        Vector3f p1 = new Vector3f(position.x + size.x * 0.5f, position.y - size.y * 0.5f, position.z);
        Vector3f p2 = new Vector3f(position.x + size.x * 0.5f, position.y + size.y * 0.5f, position.z);
        Vector3f p3 = new Vector3f(position.x - size.x * 0.5f, position.y + size.y * 0.5f, position.z);

        drawLine(p0, p1, color, entityId);
        drawLine(p1, p2, color, entityId);
        drawLine(p2, p3, color, entityId);
        drawLine(p3, p0, color, entityId);
    }

    public static void drawRect(Matrix4f transform, Vector4f color) {
        drawRect(transform, color, -1);
    }

    public static void drawRect(Matrix4f transform, Vector4f color, int entityId) {
        Vector3f[] lineVertices = new Vector3f[4];

        for (int i = 0; i < 4; i++) {
            lineVertices[i] = transformQuadPosition(transform, i);
        }

        drawLine(lineVertices[0], lineVertices[1], color, entityId);
        drawLine(lineVertices[1], lineVertices[2], color, entityId);
        drawLine(lineVertices[2], lineVertices[3], color, entityId);
        drawLine(lineVertices[3], lineVertices[0], color, entityId);
    }

    public static void drawQuad(Vector2f position, Vector2f size, Vector4f color) {
        drawQuad(new Vector3f(position.x, position.y, 0.0f), size, color);
    }

    public static void drawQuad(Vector3f position, Vector2f size, Vector4f color) {
        Matrix4f transform = new Matrix4f().translate(position.x, position.y, position.z).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        drawQuad(transform, color, -1);
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
        Matrix4f transform = new Matrix4f().translate(position.x, position.y, position.z).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        drawQuad(transform, texture, tilingFactor, tintColor, -1);
    }

    public static Vector3f transformQuadPosition(Matrix4f transform, int quadVertexPosition) {
        Vector4f transformedPosition = transform.transform(data.quadVertexPositions[quadVertexPosition], new Vector4f());
        return new Vector3f(transformedPosition.x, transformedPosition.y, transformedPosition.z);
    }

    public static void drawQuad(Matrix4f transform, Vector4f color, int entityId) {
        int quadVertexCount = 4;
        float textureIndex = 0.0f;
        Vector2f[] textureCoords = new Vector2f[]{new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 0.0f), new Vector2f(1.0f, 1.0f), new Vector2f(0.0f, 1.0f)};
        float tilingFactor = 1.0f;

        if (data.quadIndexCount >= Renderer2DData.maxIndices) {
            nextBatch();
        }

        for (int i = 0; i < quadVertexCount; i++) {
            data.quadVertexBufferBase.get(data.quadVertexBufferPtr).setQuadVertex(transformQuadPosition(transform, i), color, textureCoords[i], textureIndex, tilingFactor, entityId);
            data.quadVertexBufferPtr++;
        }

        data.quadIndexCount += 6;
        data.stats.quadCount++;
    }

    public static void drawQuad(Matrix4f transform, Texture2D texture) {
        drawQuad(transform, texture, 1.0f, new Vector4f(1.0f), -1);
    }

    public static void drawQuad(Matrix4f transform, Texture2D texture, float tilingFactor) {
        drawQuad(transform, texture, tilingFactor, new Vector4f(1.0f), -1);
    }

    public static void drawQuad(Matrix4f transform, Texture2D texture, float tilingFactor, Vector4f tintColor, int entityId) {
        int quadVertexCount = 4;
        float textureIndex = 0.0f;
        Vector2f[] textureCoords = new Vector2f[]{new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 0.0f), new Vector2f(1.0f, 1.0f), new Vector2f(0.0f, 1.0f)};

        if (data.quadIndexCount >= Renderer2DData.maxIndices) {
            nextBatch();
        }

        for (int i = 1; i < data.textureSlotIndex; i++) {
            if (data.textureSlots[i].equals(texture)) {
                textureIndex = (float) i;
                break;
            }
        }

        if (textureIndex == 0.0f) {
            if (data.textureSlotIndex >= Renderer2DData.maxTextureSlots) {
                nextBatch();
            }

            textureIndex = (float) data.textureSlotIndex;
            data.textureSlots[data.textureSlotIndex] = texture;
            data.textureSlotIndex++;
        }

        for (int i = 0; i < quadVertexCount; i++) {
            data.quadVertexBufferBase.get(data.quadVertexBufferPtr).setQuadVertex(transformQuadPosition(transform, i), tintColor, textureCoords[i], textureIndex, tilingFactor, entityId);
            data.quadVertexBufferPtr++;
        }

        data.quadIndexCount += 6;
        data.stats.quadCount++;
    }

    public static void drawRotatedQuad(Vector2f position, Vector2f size, float rotation, Vector4f color) {
        drawRotatedQuad(new Vector3f(position.x, position.y, 0.0f), size, rotation, color);
    }

    public static void drawRotatedQuad(Vector3f position, Vector2f size, float rotation, Vector4f color) {
        Matrix4f transform = new Matrix4f().translate(position.x, position.y, position.z).mul(new Matrix4f().rotate((float) Math.toRadians(rotation), new Vector3f(0.0f, 0.0f, 1.0f))).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        drawQuad(transform, color, -1);
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
        Matrix4f transform = new Matrix4f().translate(position.x, position.y, position.z).mul(new Matrix4f().rotate((float) Math.toRadians(rotation), new Vector3f(0.0f, 0.0f, 1.0f))).mul(new Matrix4f().scale(size.x, size.y, 1.0f));
        drawQuad(transform, texture, tilingFactor, tintColor, -1);
    }

    public static void drawCircle(Matrix4f transform, Vector4f color) {
        drawCircle(transform, color, 1.0f);
    }

    public static void drawCircle(Matrix4f transform, Vector4f color, float thickness) {
        drawCircle(transform, color, thickness, 0.005f);
    }

    public static void drawCircle(Matrix4f transform, Vector4f color, float thickness, float fade) {
        drawCircle(transform, color, thickness, fade, -1);
    }

    public static void drawCircle(Matrix4f transform, Vector4f color, float thickness, float fade, int entityId) {
        //Needs circle version
        /*if (data.quadIndexCount >= Renderer2DData.maxIndices) {
            nextBatch();
        }*/

        for (int i = 0; i < 4; i++) {
            Vector4f local = new Vector4f(data.quadVertexPositions[i]).mul(2.0f);
            data.circleVertexBufferBase.get(data.circleVertexBufferPtr).setCircleVertex(transformQuadPosition(transform, i), new Vector3f(local.x, local.y, local.z), color, thickness, fade, entityId);
            data.circleVertexBufferPtr++;
        }

        data.circleIndexCount += 6;
        data.stats.quadCount++;
    }

    public static void drawSprite(Matrix4f transform, SpriteRendererComponent src, int entityId) {
        if (src.texture != null) {
            drawQuad(transform, src.texture, src.tilingFactor, src.color, entityId);
        } else {
            drawQuad(transform, src.color, entityId);
        }
    }

    public static float getLineWidth() {
        return data.lineWidth;
    }

    public static void setLineWidth(float width) {
        data.lineWidth = width;
    }

    public static void resetStats() {
        data.stats = new Statistics();
    }

    public static Statistics getStats() {
        return data.stats;
    }
}
