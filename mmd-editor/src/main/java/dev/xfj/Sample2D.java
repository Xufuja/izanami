package dev.xfj;

import dev.xfj.engine.core.Layer;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.renderer.OrthographicCameraController;
import dev.xfj.engine.renderer.RenderCommand;
import dev.xfj.engine.renderer.Renderer2D;
import dev.xfj.engine.renderer.VertexArray;
import dev.xfj.engine.renderer.shader.Shader;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

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

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onUpdate(TimeStep ts) {
        cameraController.onUpdate(ts);
        RenderCommand.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RenderCommand.clear();

        Renderer2D.beginScene(cameraController.getCamera());
        Renderer2D.drawQuad(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f), new Vector4f(0.8f, 0.2f, 0.3f, 1.0f));

        Renderer2D.endScene();
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
