package dev.xfj;

import dev.xfj.engine.Layer;
import dev.xfj.engine.OrthographicCameraController;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.event.Event;
import dev.xfj.engine.renderer.RenderCommand;
import dev.xfj.engine.renderer.Renderer;
import dev.xfj.engine.renderer.Texture2D;
import dev.xfj.engine.renderer.VertexArray;
import dev.xfj.engine.renderer.buffer.BufferElement;
import dev.xfj.engine.renderer.buffer.BufferLayout;
import dev.xfj.engine.renderer.buffer.IndexBuffer;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import dev.xfj.engine.renderer.shader.Shader;
import dev.xfj.engine.renderer.shader.ShaderLibrary;
import dev.xfj.platform.opengl.OpenGLShader;
import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.file.Paths;

public class ExampleLayer extends Layer {
    public ShaderLibrary shaderLibrary;
    public Shader shader;
    public VertexArray vertexArray;

    public Shader flatColorShader;
    public VertexArray squareVA;

    public Texture2D texture;
    public Texture2D logoTexture;

    public OrthographicCameraController cameraController;
    public Vector3f squareColor;

    public ExampleLayer() throws IOException {
        super("Example Layer");
        shaderLibrary = new ShaderLibrary();
        cameraController = new OrthographicCameraController(1280.0f / 720.0f);

        squareColor = new Vector3f(0.2f, 0.3f, 0.8f);

        vertexArray = VertexArray.create();

        float[] vertices = {
                -0.5f, -0.5f, 0.0f, 0.8f, 0.2f, 0.8f, 1.0f,
                0.5f, -0.5f, 0.0f, 0.2f, 0.3f, 0.8f, 1.0f,
                0.0f, 0.5f, 0.0f, 0.8f, 0.8f, 0.2f, 1.0f
        };

        VertexBuffer vertexBuffer = VertexBuffer.create(vertices);

        BufferLayout bufferLayout = new BufferLayout(
                new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position"),
                new BufferElement(BufferElement.ShaderDataType.Float4, "a_Color")
        );
        vertexBuffer.setLayout(bufferLayout);
        vertexArray.addVertexBuffer(vertexBuffer);

        int[] indices = {0, 1, 2};
        IndexBuffer indexBuffer = IndexBuffer.create(indices, indices.length);
        vertexArray.setIndexBuffer(indexBuffer);

        squareVA = VertexArray.create();

        float[] squareVertices = {
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f,
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.0f, 1.0f, 1.0f,
                -0.5f, 0.5f, 0.0f, 0.0f, 1.0f
        };

        VertexBuffer squareVB = VertexBuffer.create(squareVertices);
        squareVB.setLayout(new BufferLayout(new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position"), new BufferElement(BufferElement.ShaderDataType.Float2, "a_TexCoord")));
        squareVA.addVertexBuffer(squareVB);

        int[] squareIndices = {0, 1, 2, 2, 3, 0};
        IndexBuffer squareIB = IndexBuffer.create(squareIndices, squareIndices.length);
        squareVA.setIndexBuffer(squareIB);

        String vertexSrc = """
                #version 330 core
                                
                layout(location = 0) in vec3 a_Position;
                layout(location = 1) in vec4 a_Color;
                                
                uniform mat4 u_ViewProjection;
                uniform mat4 u_Transform;
                                
                out vec3 v_Position;
                out vec4 v_Color;
                                
                void main()
                {
                    v_Position = a_Position;
                    v_Color = a_Color;
                    gl_Position =  u_ViewProjection * u_Transform * vec4(a_Position, 1.0);
                }
                """;

        String fragmentSrc = """
                #version 330 core
                                
                layout(location = 0) out vec4 color;
                                
                in vec3 v_Position;
                in vec4 v_Color;
                                
                void main()
                {
                    color = vec4(v_Position * 0.5 + 0.5, 1.0);
                    color = v_Color;
                }
                """;

        shader = Shader.create("VertexPosColor", vertexSrc, fragmentSrc);

        String flatColorShaderVertexSrc = """
                #version 330 core
                                
                layout(location = 0) in vec3 a_Position;
                                
                uniform mat4 u_ViewProjection;
                uniform mat4 u_Transform;
                                
                out vec3 v_Position;
                                
                void main()
                {
                    v_Position = a_Position;
                    gl_Position = u_ViewProjection * u_Transform * vec4(a_Position, 1.0);
                }
                """;
        String flatColorShaderFragmentSrc = """
                #version 330 core
                                
                layout(location = 0) out vec4 color;
                                
                in vec3 v_Position;
                                
                uniform vec3 u_Color;
                                
                void main()
                {
                    color = vec4(u_Color, 1.0);
                }
                """;
        flatColorShader = Shader.create("FlatColor", flatColorShaderVertexSrc, flatColorShaderFragmentSrc);

        Shader textureShader = shaderLibrary.load(Paths.get("assets", "shaders", "Texture.glsl"));
        texture = Texture2D.create(Paths.get("assets", "textures", "Checkerboard.png"));
        logoTexture = Texture2D.create(Paths.get("assets", "textures", "Logo.png"));

        textureShader.bind();
        ((OpenGLShader) textureShader).uploadUniformInt("u_Texture", 0);
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onUpdate(TimeStep ts) {
        cameraController.onUpdate(ts);
        RenderCommand.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RenderCommand.clear();

        Renderer.beginScene(cameraController.getCamera());

        Matrix4f scale = new Matrix4f().scale(0.1f);

        flatColorShader.bind();
        ((OpenGLShader) flatColorShader).uploadUniformFloat3("u_Color", squareColor);

        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                Vector3f pos = new Vector3f(x * 0.11f, y * 0.11f, 0.0f);
                Matrix4f transform = new Matrix4f().translate(pos).mul(scale);
                Renderer.submit(flatColorShader, squareVA, transform);
            }
        }

        Shader textureShader = shaderLibrary.get("Texture");

        texture.bind();
        Renderer.submit(textureShader, squareVA, new Matrix4f().scale(1.51f));
        logoTexture.bind();
        Renderer.submit(textureShader, squareVA, new Matrix4f().scale(1.51f));
        //Renderer.submit(shader, vertexArray);

        Renderer.endScene();
    }

    @Override
    public void onImGuiRender() {
        ImGui.begin("Settings");
        //There is no equivalent to glm::value_ptr(m_SquareColor) so doing it this way
        float[] newColor = {squareColor.x, squareColor.y, squareColor.z};
        ImGui.colorEdit3("Square Color", newColor);
        squareColor = new Vector3f(newColor[0], newColor[1], newColor[2]);
        ImGui.end();
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);
    }
}
