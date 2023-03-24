package dev.xfj;

import dev.xfj.engine.core.Layer;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.renderer.*;
import dev.xfj.engine.renderer.buffer.BufferElement;
import dev.xfj.engine.renderer.buffer.BufferLayout;
import dev.xfj.engine.renderer.buffer.IndexBuffer;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import dev.xfj.engine.renderer.shader.Shader;
import dev.xfj.platform.opengl.OpenGLShader;
import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.file.Path;

public class Sample2D extends Layer {
    private OrthographicCameraController cameraController;
    private Shader flatColorShader;
    private VertexArray squareVA;
    private Vector4f squareColor;

    public Sample2D() {
        super("Sample 2D");
        this.cameraController = new OrthographicCameraController(1280.0f / 720.0f, true);
        this.squareColor = new Vector4f(0.2f, 0.3f, 0.8f, 1.0f);
    }

    @Override
    public void onAttach() {
        squareVA = VertexArray.create();

        float[] squareVertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f
        };

        VertexBuffer squareVB = VertexBuffer.create(squareVertices);
        squareVB.setLayout(new BufferLayout(new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position")));
        squareVA.addVertexBuffer(squareVB);

        int[] squareIndices = {0, 1, 2, 2, 3, 0};
        IndexBuffer squareIB = IndexBuffer.create(squareIndices, squareIndices.length);
        squareVA.setIndexBuffer(squareIB);
        try {
            flatColorShader = Shader.create(Path.of("assets/shaders/FlatColor.glsl"));
        } catch (IOException e) {
            Log.error(e.toString());
        }
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

        flatColorShader.bind();
        ((OpenGLShader) flatColorShader).uploadUniformFloat4("u_Color", squareColor);

        Renderer.submit(flatColorShader, squareVA, new Matrix4f().scale(1.5f));

        Renderer.endScene();
    }

    @Override
    public void onImGuiRender() {
        ImGui.begin("Settings");
        //There is no equivalent to glm::value_ptr(m_SquareColor) so doing it this way
        float[] newColor = {squareColor.x, squareColor.y, squareColor.z, squareColor.w};
        ImGui.colorEdit4("Square Color", newColor);
        squareColor = new Vector4f(newColor[0], newColor[1], newColor[2], newColor[3]);
        ImGui.end();
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);
    }
}
