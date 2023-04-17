package dev.xfj;

import dev.xfj.engine.core.Application;
import dev.xfj.engine.core.Layer;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.renderer.*;
import dev.xfj.engine.renderer.framebuffer.Framebuffer;
import dev.xfj.engine.renderer.framebuffer.FramebufferSpecification;
import dev.xfj.engine.renderer.renderer2d.Renderer2D;
import dev.xfj.engine.renderer.renderer2d.Statistics;
import dev.xfj.engine.renderer.shader.Shader;
import dev.xfj.engine.scene.Entity;
import dev.xfj.engine.scene.Scene;
import dev.xfj.engine.scene.SceneCamera;
import dev.xfj.engine.scene.components.*;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.file.Path;

public class EditorLayer extends Layer {
    private static float rotation = 0.0f;
    private static boolean opt_fullscreen_persistant = true;
    private static int dockspace_flags = ImGuiDockNodeFlags.None;

    private final OrthographicCameraController cameraController;
    private Shader flatColorShader;
    private VertexArray squareVA;
    private Framebuffer framebuffer;
    private Scene activeScene;
    private Entity squareEntity;
    private Entity cameraEntity;
    private Entity secondCamera;
    private boolean primaryCamera;
    private Texture2D checkerBoardTexture;
    private boolean viewportFocused;
    private boolean viewportHovered;
    private final Vector2f viewportSize;
    private Vector4f squareColor;

    static {
        System.setProperty("dominion.show-banner", "false");
        System.setProperty("dominion.logging-level", "INFO");
        System.setProperty("dominion.dominion-1.logging-level", "INFO");
    }

    public EditorLayer() {
        super("Sample 2D");
        this.cameraController = new OrthographicCameraController(1280.0f / 720.0f, true);
        this.viewportFocused = false;
        this.viewportHovered = false;
        this.viewportSize = new Vector2f(0.0f, 0.0f);
        this.squareColor = new Vector4f(0.2f, 0.3f, 0.8f, 1.0f);
        this.primaryCamera = true;
    }

    @Override
    public void onAttach() {
        checkerBoardTexture = Texture2D.create(Path.of("assets/textures/Checkerboard.png"));

        FramebufferSpecification fbSpec = new FramebufferSpecification();
        fbSpec.width = 1280;
        fbSpec.height = 720;
        framebuffer = Framebuffer.create(fbSpec);

        activeScene = new Scene();

        Entity square = activeScene.createEntity("Green Square");
        square.addComponent(new SpriteRendererComponent(new Vector4f(0.0f, 1.0f, 0.0f, 1.0f)));
        squareEntity = square;

        cameraEntity = activeScene.createEntity("Camera Entity");
        cameraEntity.addComponent(new CameraComponent());

        secondCamera = activeScene.createEntity("Clip-Space Entity");
        secondCamera.addComponent(new CameraComponent());
        secondCamera.getComponent(CameraComponent.class).primary = false;

        cameraEntity.addComponent(new NativeScriptComponent<CameraController>());
        cameraEntity.getComponent(NativeScriptComponent.class).bind(CameraController.class);
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onUpdate(TimeStep ts) {
        FramebufferSpecification spec = framebuffer.getSpecification();
        if (viewportSize.x > 0.0f && viewportSize.y > 0.0f && (spec.width != viewportSize.x || spec.height != viewportSize.y)) {
            framebuffer.resize((int) viewportSize.x, (int) viewportSize.y);
            cameraController.onResize(viewportSize.x, viewportSize.y);
            activeScene.onViewportResize((int) viewportSize.x, (int) viewportSize.y);
        }

        if (viewportFocused) {
            cameraController.onUpdate(ts);
        }

        Renderer2D.resetStats();
        framebuffer.bind();
        RenderCommand.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RenderCommand.clear();

        /*rotation += ts.getTime() * 50.0f;

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

        Renderer2D.endScene();*/

        activeScene.onUpdate(ts);

        framebuffer.unbind();
    }

    @Override
    public void onImGuiRender() {
        boolean dockspaceOpen = true;
        boolean opt_fullscreen = opt_fullscreen_persistant;

        int window_flags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        if (opt_fullscreen) {
            ImGuiViewport viewport = ImGui.getMainViewport();
            ImGui.setNextWindowPos(viewport.getPosX(), viewport.getPosY());
            ImGui.setNextWindowSize(viewport.getSizeX(), viewport.getSizeX());
            ImGui.setNextWindowViewport(viewport.getID());
            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
            window_flags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove;
            window_flags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
        }

        if ((dockspace_flags & ImGuiDockNodeFlags.PassthruCentralNode) != 0) {
            window_flags |= ImGuiWindowFlags.NoBackground;
        }

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin("DockSpace Demo", new ImBoolean(dockspaceOpen), window_flags);
        ImGui.popStyleVar();

        if (opt_fullscreen) {
            ImGui.popStyleVar(2);
        }

        final ImGuiIO io = ImGui.getIO();
        if (io.hasConfigFlags(ImGuiConfigFlags.DockingEnable)) {
            int dockspace_id = ImGui.getID("MyDockSpace");
            ImGui.dockSpace(dockspace_id, 0.0f, 0.0f, dockspace_flags);
        }

        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Exit")) {
                    Application.getApplication().close();
                }
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }
        ImGui.begin("Settings");
        Statistics stats = Renderer2D.getStats();
        ImGui.text("Renderer2D Stats:");
        ImGui.text(String.format("Draw Calls: %1$d", stats.drawCalls));
        ImGui.text(String.format("Quads: %1$d", stats.quadCount));
        ImGui.text(String.format("Vertices: %1$d", stats.getTotalVertexCount()));
        ImGui.text(String.format("Indices: %1$d", stats.getTotalIndexCount()));

        if (squareEntity != null) {
            ImGui.separator();
            String tag = squareEntity.getComponent(TagComponent.class).tag;
            ImGui.text(tag);

            //There is no equivalent to glm::value_ptr(m_SquareColor) so doing it this way
            squareColor = squareEntity.getComponent(SpriteRendererComponent.class).color;
            float[] newColor = {squareColor.x, squareColor.y, squareColor.z, squareColor.w};
            ImGui.colorEdit4("Square Color", newColor);
            squareColor = new Vector4f(newColor[0], newColor[1], newColor[2], newColor[3]);
            squareEntity.getComponent(SpriteRendererComponent.class).color = squareColor;
            ImGui.separator();
        }

        Vector4f cameraTransform = cameraEntity.getComponent(TransformComponent.class).transform.getColumn(3, new Vector4f());
        float[] newCameraTransform = {cameraTransform.x, cameraTransform.y, cameraTransform.z};
        ImGui.dragFloat3("Camera Transform", newCameraTransform);
        cameraEntity.getComponent(TransformComponent.class).transform.setColumn(3, new Vector4f(newCameraTransform[0], newCameraTransform[1], newCameraTransform[2], cameraTransform.w));

        if (ImGui.checkbox("Camera A", primaryCamera)) {
            primaryCamera = !primaryCamera;
            cameraEntity.getComponent(CameraComponent.class).primary = primaryCamera;
            secondCamera.getComponent(CameraComponent.class).primary = !primaryCamera;
        }

        SceneCamera camera = secondCamera.getComponent(CameraComponent.class).camera;
        float[] orthoSize = new float[]{camera.getOrthographicSize()};
        if (ImGui.dragFloat("Second Camera Ortho Size", orthoSize)) {
            camera.setOrthographicSize(orthoSize[0]);
        }

        ImGui.end();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin("Viewport");

        viewportFocused = ImGui.isWindowFocused();
        viewportHovered = ImGui.isWindowHovered();
        Application.getApplication().getImGuiLayer().blockEvents(!viewportFocused || !viewportHovered);

        ImVec2 viewportPanelSize = ImGui.getContentRegionAvail();

        viewportSize.x = viewportPanelSize.x;
        viewportSize.y = viewportPanelSize.y;

        int textureId = framebuffer.getColorAttachmentRendererId();
        ImGui.image(textureId, viewportSize.x, viewportSize.y, 0, 1, 1, 0);
        ImGui.end();
        ImGui.popStyleVar();

        ImGui.end();
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);
    }
}
