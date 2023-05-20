package dev.xfj.engine.renderer.renderer2d;

import dev.xfj.engine.renderer.Texture2D;
import dev.xfj.engine.renderer.UniformBuffer;
import dev.xfj.engine.renderer.VertexArray;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import dev.xfj.engine.renderer.shader.Shader;
import org.joml.Vector4f;

import java.util.List;

public class Renderer2DData {
    public static final int maxQuads = 20000;
    public static final int maxVertices = maxQuads * 4;
    public static final int maxIndices = maxQuads * 6;
    public static final int maxTextureSlots = 32;

    public VertexArray quadVertexArray;
    public VertexBuffer quadVertexBuffer;
    public Shader textureShader;
    public Texture2D whiteTexture;

    public int quadIndexCount = 0;
    public List<QuadVertex> quadVertexBufferBase;
    public int quadVertexBufferPtr;
    public Texture2D[] textureSlots = new Texture2D[maxTextureSlots];
    public int textureSlotIndex = 1;

    public Vector4f[] quadVertexPositions = new Vector4f[4];

    public Statistics stats = new Statistics();

    CameraData cameraBuffer = new CameraData();
    UniformBuffer cameraUniformBuffer;
}
