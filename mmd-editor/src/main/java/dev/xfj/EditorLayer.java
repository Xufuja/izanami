package dev.xfj;

import dev.xfj.engine.core.*;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.events.EventDispatcher;
import dev.xfj.engine.events.key.KeyPressedEvent;
import dev.xfj.engine.renderer.*;
import dev.xfj.engine.renderer.framebuffer.Framebuffer;
import dev.xfj.engine.renderer.framebuffer.FramebufferSpecification;
import dev.xfj.engine.renderer.framebuffer.FramebufferTextureSpecification;
import dev.xfj.engine.renderer.renderer2d.Renderer2D;
import dev.xfj.engine.renderer.renderer2d.Statistics;
import dev.xfj.engine.renderer.shader.Shader;
import dev.xfj.engine.scene.Entity;
import dev.xfj.engine.scene.Scene;
import dev.xfj.engine.scene.SceneSerializer;
import dev.xfj.engine.scene.components.TagComponent;
import dev.xfj.engine.scene.components.TransformComponent;
import dev.xfj.panels.SceneHierarchyPanel;
import dev.xfj.platform.windows.WindowsPlatformUtils;
import imgui.*;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.file.Path;
import java.util.Optional;

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
    private Entity hoveredEntity;
    private boolean primaryCamera;
    private EditorCamera editorCamera;
    private Texture2D checkerBoardTexture;
    private boolean viewportFocused;
    private boolean viewportHovered;
    private final Vector2f viewportSize;
    private Vector2f[] viewportBounds;
    private Vector4f squareColor;
    private int gizmoType;

    private SceneHierarchyPanel sceneHierarchyPanel;

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
        this.viewportBounds = new Vector2f[]{new Vector2f(0.0f, 0.0f), new Vector2f(0.0f, 0.0f)};
        this.squareColor = new Vector4f(0.2f, 0.3f, 0.8f, 1.0f);
        this.primaryCamera = true;
        this.sceneHierarchyPanel = new SceneHierarchyPanel();
        this.gizmoType = -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach() {
        checkerBoardTexture = Texture2D.create(Path.of("assets/textures/Checkerboard.png"));

        FramebufferSpecification fbSpec = new FramebufferSpecification();
        fbSpec.attachments.attachments.add(new FramebufferTextureSpecification(Framebuffer.FramebufferTextureFormat.RGBA8));
        fbSpec.attachments.attachments.add(new FramebufferTextureSpecification(Framebuffer.FramebufferTextureFormat.RED_INTEGER));
        fbSpec.attachments.attachments.add(new FramebufferTextureSpecification(Framebuffer.FramebufferTextureFormat.Depth));
        fbSpec.width = 1280;
        fbSpec.height = 720;
        framebuffer = Framebuffer.create(fbSpec);

        activeScene = new Scene();
        editorCamera = new EditorCamera(30.0f, 1.778f, 0.1f, 1000.0f);

        /*Entity square = activeScene.createEntity("Green Square");
        square.addComponent(new SpriteRendererComponent(new Vector4f(0.0f, 1.0f, 0.0f, 1.0f)));
        squareEntity = square;

        Entity redSquare = activeScene.createEntity("Red Square");
        redSquare.addComponent(new SpriteRendererComponent(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f)));

        cameraEntity = activeScene.createEntity("Camera A");
        cameraEntity.addComponent(new CameraComponent());

        secondCamera = activeScene.createEntity("Camera B");
        secondCamera.addComponent(new CameraComponent());
        secondCamera.getComponent(CameraComponent.class).primary = false;

        cameraEntity.addComponent(new NativeScriptComponent<CameraController>());
        cameraEntity.getComponent(NativeScriptComponent.class).bind(CameraController.class);

        secondCamera.addComponent(new NativeScriptComponent<CameraController>());
        secondCamera.getComponent(NativeScriptComponent.class).bind(CameraController.class);*/

        sceneHierarchyPanel.setContext(activeScene);
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
            editorCamera.setViewportSize(viewportSize.x, viewportSize.y);
            activeScene.onViewportResize((int) viewportSize.x, (int) viewportSize.y);
        }

        if (viewportFocused) {
            cameraController.onUpdate(ts);
        }

        editorCamera.onUpdate(ts);

        Renderer2D.resetStats();
        framebuffer.bind();
        RenderCommand.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RenderCommand.clear();

        framebuffer.clearAttachment(1, -1);

        //activeScene.onUpdateRuntime(ts);
        activeScene.onUpdateEditor(ts, editorCamera);

        ImVec2 mousePosition = ImGui.getMousePos();
        mousePosition.x -= viewportBounds[0].x;
        mousePosition.y -= viewportBounds[0].y;
        Vector2f viewportSize = viewportBounds[1].sub(viewportBounds[0], new Vector2f());
        mousePosition.y = viewportSize.y - mousePosition.y;

        int mouseX = (int) mousePosition.x;
        int mouseY = (int) mousePosition.y;

        if (mouseX >= 0 && mouseY >= 0 && mouseX < (int) viewportSize.x && mouseY < (int) viewportSize.y) {
            float pixelData = framebuffer.readPixel(1, mouseX, mouseY);
            hoveredEntity = activeScene.getEntityById(pixelData);
        }

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
        ImGuiStyle style = ImGui.getStyle();
        ImVec2 minWinSize = style.getWindowMinSize();
        float minWinSizeX = minWinSize.x;
        style.setWindowMinSize(370.0f, minWinSize.y);

        if (io.hasConfigFlags(ImGuiConfigFlags.DockingEnable)) {
            int dockspace_id = ImGui.getID("MyDockSpace");
            ImGui.dockSpace(dockspace_id, 0.0f, 0.0f, dockspace_flags);
        }
        style.setWindowMinSize(minWinSizeX, style.getWindowMinSizeY());

        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {

                if (ImGui.menuItem("New", "Ctrl+N")) {
                    newScene();
                }

                if (ImGui.menuItem("Open...", "Ctrl+O")) {
                    openScene();
                }

                if (ImGui.menuItem("Save As...", "Ctrl+Shift+S")) {
                    saveSceneAs();
                }

                if (ImGui.menuItem("Exit")) {
                    Application.getApplication().close();
                }

                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }

        sceneHierarchyPanel.onImGuiRender();

        ImGui.begin("Stats");

        String name = "None";
        if (hoveredEntity != null) {
            name = hoveredEntity.getComponent(TagComponent.class).tag;
        }
        ImGui.text(String.format("Hovered Entity: %s", name));

        Statistics stats = Renderer2D.getStats();
        ImGui.text("Renderer2D Stats:");
        ImGui.text(String.format("Draw Calls: %1$d", stats.drawCalls));
        ImGui.text(String.format("Quads: %1$d", stats.quadCount));
        ImGui.text(String.format("Vertices: %1$d", stats.getTotalVertexCount()));
        ImGui.text(String.format("Indices: %1$d", stats.getTotalIndexCount()));

        ImGui.end();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin("Viewport");

        ImVec2 viewportOffset = ImGui.getCursorPos();

        viewportFocused = ImGui.isWindowFocused();
        viewportHovered = ImGui.isWindowHovered();
        Application.getApplication().getImGuiLayer().blockEvents(!viewportFocused && !viewportHovered);

        ImVec2 viewportPanelSize = ImGui.getContentRegionAvail();

        viewportSize.x = viewportPanelSize.x;
        viewportSize.y = viewportPanelSize.y;

        int textureId = framebuffer.getColorAttachmentRendererId();
        ImGui.image(textureId, viewportSize.x, viewportSize.y, 0, 1, 1, 0);

        ImVec2 windowSize = ImGui.getWindowSize();
        ImVec2 minBound = ImGui.getWindowPos();
        minBound.x += viewportOffset.x;
        minBound.y += viewportOffset.y;
        ImVec2 maxBound = new ImVec2(minBound.x + viewportSize.x, minBound.y + viewportSize.y);
        viewportBounds[0] = new Vector2f(minBound.x, minBound.y);
        viewportBounds[1] = new Vector2f(maxBound.x, maxBound.y);

        Entity selectedEntity = sceneHierarchyPanel.getSelectedEntity();
        if (selectedEntity != null && gizmoType != -1) {
            ImGuizmo.setOrthographic(false);
            ImGuizmo.setDrawList();

            float windowWidth = ImGui.getWindowWidth();
            float windowHeight = ImGui.getWindowHeight();
            ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), windowWidth, windowHeight);

            //Entity cameraEntity = activeScene.getPrimaryCameraEntity();
            //SceneCamera camera = cameraEntity.getComponent(CameraComponent.class).camera;
            //Matrix4f cameraProjection = camera.getProjection();
            //Matrix4f cameraView = cameraEntity.getComponent(TransformComponent.class).getTransform().invert();

            Matrix4f cameraProjection = editorCamera.getProjection();
            Matrix4f cameraView = editorCamera.getViewMatrix();

            TransformComponent transformComponent = selectedEntity.getComponent(TransformComponent.class);
            Matrix4f transform = transformComponent.getTransform();

            boolean snap = Input.isKeyPressed(KeyCodes.LEFT_CONTROL);
            float snapValue = 0.5f;

            if (gizmoType == Operation.ROTATE) {
                snapValue = 45.0f;
            }

            float[] snapValues = new float[]{snapValue, snapValue, snapValue};

            float[] transformAsFloatArray = transform.get(new float[16]);

            ImGuizmo.manipulate(cameraView.get(new float[16]), cameraProjection.get(new float[16]), transformAsFloatArray, gizmoType, Mode.LOCAL, snap ? snapValues : new float[]{0});

            transform.set(transformAsFloatArray);

            if (ImGuizmo.isUsing()) {
                Vector3f translation = new Vector3f();
                Vector3f rotation = new Vector3f();
                Vector3f scale = new Vector3f();

                transform.getTranslation(translation);
                transform.getEulerAnglesXYZ(rotation);
                transform.getScale(scale);

                Vector3f deltaRotation = rotation.sub(transformComponent.rotation, new Vector3f());
                transformComponent.translation = translation;
                transformComponent.rotation.add(deltaRotation);
                transformComponent.scale = scale;
            }
        }

        ImGui.end();
        ImGui.popStyleVar();

        ImGui.end();
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);
        editorCamera.onEvent(event);
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        eventDispatcher.dispatch(KeyPressedEvent.class, this::onKeyPressed);
    }

    private boolean onKeyPressed(KeyPressedEvent event) {
        if (event.getRepeatCount() > 0) {
            return false;
        }

        boolean control = Input.isKeyPressed(KeyCodes.LEFT_CONTROL) || Input.isKeyPressed(KeyCodes.RIGHT_CONTROL);
        boolean shift = Input.isKeyPressed(KeyCodes.LEFT_SHIFT) || Input.isKeyPressed(KeyCodes.RIGHT_SHIFT);

        switch (event.getKeyCode()) {
            case KeyCodes.N -> {
                if (control) {
                    newScene();
                    return true;
                }
            }
            case KeyCodes.O -> {
                if (control) {
                    openScene();
                    return true;
                }
            }
            case KeyCodes.S -> {
                if (control && shift) {
                    saveSceneAs();
                    return true;
                }
            }
            case KeyCodes.Q ->  {
                if (!ImGuizmo.isUsing()) {
                    gizmoType = -1;
                }
            }
            case KeyCodes.W -> {
                if (!ImGuizmo.isUsing()) {
                    gizmoType = Operation.TRANSLATE;
                }
            }
            case KeyCodes.E -> {
                if (!ImGuizmo.isUsing()) {
                    gizmoType = Operation.ROTATE;
                }
            }
            case KeyCodes.R -> {
                if (!ImGuizmo.isUsing()) {
                    gizmoType = Operation.SCALE;
                }
            }
        }
        return false;
    }

    private void newScene() {
        activeScene = new Scene();
        activeScene.onViewportResize((int) viewportSize.x, (int) viewportSize.y);
        sceneHierarchyPanel.setContext(activeScene);
    }

    private void openScene() {
        Optional<String> filePath = WindowsPlatformUtils.openFile("Scene (*.scene)\0*.scene\0");

        if (filePath.isPresent()) {
            activeScene = new Scene();
            activeScene.onViewportResize((int) viewportSize.x, (int) viewportSize.y);
            sceneHierarchyPanel.setContext(activeScene);

            SceneSerializer serializer = new SceneSerializer(activeScene);
            serializer.deserialize(Path.of(filePath.get()));
        }
    }

    private void saveSceneAs() {
        Optional<String> filePath = WindowsPlatformUtils.saveFile("Scene (*.scene)\0*.scene\0");

        if (filePath.isPresent()) {
            SceneSerializer serializer = new SceneSerializer(activeScene);
            serializer.serialize(Path.of(filePath.get()));
        }
    }
}
