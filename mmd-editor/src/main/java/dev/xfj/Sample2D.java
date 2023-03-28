package dev.xfj;

import dev.xfj.engine.core.Layer;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.renderer.*;
import dev.xfj.engine.renderer.shader.Shader;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Sample2D extends Layer {
    private final OrthographicCameraController cameraController;
    private Shader flatColorShader;
    private VertexArray squareVA;
    private Texture2D checkerBoardTexture;
    private Vector4f squareColor;
    private final List<ProfileResult> profileResults;

    public Sample2D() {
        super("Sample 2D");
        this.cameraController = new OrthographicCameraController(1280.0f / 720.0f, true);
        this.squareColor = new Vector4f(0.2f, 0.3f, 0.8f, 1.0f);
        this.profileResults = new ArrayList<>();
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
        Timer.profileScope("Sandbox2D::OnUpdate", profileResults::add, () -> {
            Timer.profileScope("CameraController::OnUpdate", profileResults::add, () -> {
                cameraController.onUpdate(ts);
            });

            Timer.profileScope("Renderer Prep", profileResults::add, () -> {
                RenderCommand.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
                RenderCommand.clear();
            });
            Timer.profileScope("Renderer Draw", profileResults::add, () -> {
                //Either the profiling is not working correctly, or the draw calls in the Java version are indeed 30x slower
                Renderer2D.beginScene(cameraController.getCamera());
                Renderer2D.drawQuad(new Vector2f(-1.0f, 0.0f), new Vector2f(0.8f, 0.8f), new Vector4f(0.8f, 0.2f, 0.3f, 1.0f));
                Renderer2D.drawQuad(new Vector2f(0.5f, -0.5f), new Vector2f(0.5f, 0.75f), squareColor);
                Renderer2D.drawQuad(new Vector3f(0.0f, 0.0f, -0.1f), new Vector2f(10.0f, 10.0f), checkerBoardTexture);
                Renderer2D.endScene();
            });
        });
    }

    @Override
    public void onImGuiRender() {
        ImGui.begin("Settings");
        //There is no equivalent to glm::value_ptr(m_SquareColor) so doing it this way
        float[] newColor = {squareColor.x, squareColor.y, squareColor.z, squareColor.w};
        ImGui.colorEdit4("Square Color", newColor);
        squareColor = new Vector4f(newColor[0], newColor[1], newColor[2], newColor[3]);

        for (ProfileResult profileResult : profileResults) {
            String label = String.format("%1$.3fms %2$s", profileResult.time, profileResult.name);
            ImGui.text(label);
        }
        profileResults.clear();
        ImGui.end();
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);
    }
}
