package dev.xfj.engine.renderer.renderer2d;

import dev.xfj.engine.renderer.Texture2D;
import dev.xfj.engine.renderer.VertexArray;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import dev.xfj.engine.renderer.shader.Shader;

import java.util.List;

public class Renderer2DData {
    public static int maxTextureSlots = 32;
    public final int maxQuads = 10000;
    public final int maxVertices = maxQuads * 4;
    public final int maxIndices = maxQuads * 6;

    public VertexArray quadVertexArray;
    public VertexBuffer quadVertexBuffer;
    public Shader textureShader;
    public Texture2D whiteTexture;

    public int quadIndexCount = 0;
    public List<QuadVertex> quadVertexBufferBase;
    public int quadVertexBufferPtr;
    public Texture2D[] textureSlots = new Texture2D[maxTextureSlots];
    public int textureSlotIndex = 1;

}
