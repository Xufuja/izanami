package dev.xfj;

import dev.xfj.engine.core.Layer;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.renderer.OrthographicCameraController;
import dev.xfj.engine.renderer.RenderCommand;
import dev.xfj.engine.renderer.Texture2D;
import dev.xfj.engine.renderer.VertexArray;
import dev.xfj.engine.renderer.renderer2d.Renderer2D;
import dev.xfj.engine.renderer.renderer2d.Statistics;
import dev.xfj.engine.renderer.shader.Shader;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.file.Path;

public class Sample2D extends Layer {
    private static float rotation = 0.0f;
    private final OrthographicCameraController cameraController;
    private Shader flatColorShader;
    private VertexArray squareVA;
    private Texture2D checkerBoardTexture;
    private Vector4f squareColor;

    public Sample2D() {
        super("Sample 2D");
        this.cameraController = new OrthographicCameraController(1280.0f / 720.0f, true);
        this.squareColor = new Vector4f(0.2f, 0.3f, 0.8f, 1.0f);
    }

    @Override
    public void onAttach() {
        checkerBoardTexture = Texture2D.create(Path.of("assets/textures/Checkerboard.png"));
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onUpdate(TimeStep ts) {
        cameraController.onUpdate(ts);

        Renderer2D.resetStats();

        RenderCommand.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RenderCommand.clear();
        rotation += ts.getTime() * 50.0f;

        Renderer2D.beginScene(cameraController.getCamera());
        Renderer2D.drawRotatedQuad(new Vector2f(1.0f, 0.0f), new Vector2f(0.8f, 0.8f), -45.0f, new Vector4f(0.8f, 0.2f, 0.3f, 1.0f));
        Renderer2D.drawQuad(new Vector2f(-1.0f, 0.0f), new Vector2f(0.8f, 0.8f), new Vector4f(0.8f, 0.2f, 0.3f, 1.0f));
        Renderer2D.drawQuad(new Vector2f(0.5f, -0.5f), new Vector2f(0.5f, 0.75f), squareColor);
        Renderer2D.drawQuad(new Vector3f(0.0f, 0.0f, -0.1f), new Vector2f(20.0f, 20.0f), checkerBoardTexture, 10.0f);
        Renderer2D.drawRotatedQuad(new Vector3f(-2.0f, 0.0f, 0.0f), new Vector2f(1.0f, 1.0f), rotation, checkerBoardTexture, 20.0f);
        Renderer2D.endScene();

        Renderer2D.beginScene(cameraController.getCamera());
        for (float y = -5.0f; y < 5.0f; y += 0.5f) {
            for (float x = -5.0f; x < 5.0f; x += 0.5f) {
                Vector4f color = new Vector4f((x + 5.0f) / 10.0f, 0.4f, (y + 5.0f) / 10.0f, 0.7f);
                Renderer2D.drawQuad(new Vector2f(x, y), new Vector2f(0.45f, 0.45f), color);
            }
        }
        Renderer2D.endScene();
    }

    @Override
    public void onImGuiRender() {
        ImGui.begin("Settings");
        Statistics stats = Renderer2D.getStats();
        ImGui.text("Renderer2D Stats:");
        ImGui.text(String.format("Draw Calls: %1$d", stats.drawCalls));
        ImGui.text(String.format("Quads: %1$d", stats.quadCount));
        ImGui.text(String.format("Vertices: %1$d", stats.getTotalVertexCount()));
        ImGui.text(String.format("Indices: %1$d", stats.getTotalIndexCount()));

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
