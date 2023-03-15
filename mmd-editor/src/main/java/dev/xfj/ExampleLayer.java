package dev.xfj;

import dev.xfj.engine.Layer;
import dev.xfj.engine.event.Event;
import dev.xfj.engine.renderer.*;
import dev.xfj.engine.renderer.buffer.BufferElement;
import dev.xfj.engine.renderer.buffer.BufferLayout;
import dev.xfj.engine.renderer.buffer.IndexBuffer;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ExampleLayer extends Layer {
    public Shader shader;
    public VertexArray vertexArray;

    public Shader blueShader;
    public VertexArray squareVA;

    public OrthographicCamera camera;

    public ExampleLayer() {
        super("Example Layer");
        camera = new OrthographicCamera(-1.6f, 1.6f, -0.9f, 0.9f);
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
                -0.75f, -0.75f, 0.0f,
                0.75f, -0.75f, 0.0f,
                0.75f, 0.75f, 0.0f,
                -0.75f, 0.75f, 0.0f
        };

        VertexBuffer squareVB = VertexBuffer.create(squareVertices);
        squareVB.setLayout(new BufferLayout(new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position")));
        squareVA.addVertexBuffer(squareVB);

        int[] squareIndices = {0, 1, 2, 2, 3, 0};
        IndexBuffer squareIB = IndexBuffer.create(squareIndices, squareIndices.length);
        squareVA.setIndexBuffer(squareIB);

        String vertexSrc = """
                #version 330 core
                layout(location = 0) in vec3 a_Position;
                layout(location = 1) in vec4 a_Color;
                uniform mat4 u_ViewProjection;
                out vec3 v_Position;
                out vec4 v_Color;
                void main()
                {
                    v_Position = a_Position;
                    v_Color = a_Color;
                    gl_Position =  u_ViewProjection * vec4(a_Position, 1.0);
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

        shader = new Shader(vertexSrc, fragmentSrc);

        String blueShaderVertexSrc = """
                #version 330 core
                layout(location = 0) in vec3 a_Position;
                uniform mat4 u_ViewProjection;
                out vec3 v_Position;
                void main()
                {
                    v_Position = a_Position;
                    gl_Position = u_ViewProjection * vec4(a_Position, 1.0);
                }
                """;
        String blueShaderFragmentSrc = """
                #version 330 core
                layout(location = 0) out vec4 color;
                in vec3 v_Position;
                void main()
                {
                    color = vec4(0.2, 0.3, 0.8, 1.0);
                }
                """;
        blueShader = new Shader(blueShaderVertexSrc, blueShaderFragmentSrc);
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onUpdate() {
        RenderCommand.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RenderCommand.clear();

        camera.setPosition(new Vector3f(0.5f, 0.5f, 0.0f));
        camera.setRotation(45.0f);

        Renderer.beginScene(camera);

        Renderer.submit(blueShader, squareVA);
        Renderer.submit(shader, vertexArray);

        Renderer.endScene();
    }

    @Override
    public void onImGuiRender() {

    }

    @Override
    public void onEvent(Event event) {

    }
}
