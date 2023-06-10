package dev.xfj;

import dev.xfj.engine.core.*;
import dev.xfj.engine.core.application.Application;
import dev.xfj.engine.core.application.ApplicationCommandLineArgs;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.events.EventDispatcher;
import dev.xfj.engine.events.key.KeyPressedEvent;
import dev.xfj.engine.events.mouse.MouseButtonPressedEvent;
import dev.xfj.engine.imgui.ImGuiPayload;
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
import dev.xfj.engine.scene.components.*;
import dev.xfj.panels.ContentBrowserPanel;
import dev.xfj.panels.SceneHierarchyPanel;
import dev.xfj.platform.windows.WindowsPlatformUtils;
import imgui.*;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.*;
import imgui.type.ImBoolean;
import org.joml.*;

import java.nio.file.Path;
import java.nio.file.Paths;
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
    private Scene editorScene;
    private Path editorScenePath;
    private Entity squareEntity;
    private Entity cameraEntity;
    private Entity secondCamera;
    private Entity hoveredEntity;
    private boolean primaryCamera;
    private EditorCamera editorCamera;
    private Texture2D checkerBoardTexture;
    private Texture2D iconPlay;
    private Texture2D iconStop;
    private Texture2D iconSimulate;
    private boolean viewportFocused;
    private boolean viewportHovered;
    private final Vector2f viewportSize;
    private Vector2f[] viewportBounds;
    private Vector4f squareColor;
    private int gizmoType;
    private boolean showPhysicsColliders;
    private SceneState sceneState;

    private final SceneHierarchyPanel sceneHierarchyPanel;
    private final ContentBrowserPanel contentBrowserPanel;

    static {
        System.setProperty("dominion.show-banner", "false");
        System.setProperty("dominion.logging-level", "INFO");
        System.setProperty("dominion.dominion-1.logging-level", "INFO");
    }

    public enum SceneState {
        Edit,
        Play,
        Simulate
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
        this.contentBrowserPanel = new ContentBrowserPanel();
        this.gizmoType = -1;
        this.showPhysicsColliders = false;
        this.sceneState = SceneState.Edit;
    }

    @Override
    public void onAttach() {
        checkerBoardTexture = Texture2D.create(Path.of("assets/textures/Checkerboard.png"));

        ClassLoader classLoader = EditorLayer.class.getClassLoader();
        try {
            iconPlay = Texture2D.create(Paths.get(classLoader.getResource("icons/PlayButton.png").toURI()));
            iconStop = Texture2D.create(Paths.get(classLoader.getResource("icons/StopButton.png").toURI()));
            iconSimulate = Texture2D.create(Paths.get(classLoader.getResource("icons/SimulateButton.png").toURI()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        FramebufferSpecification fbSpec = new FramebufferSpecification();

        fbSpec.attachments.attachments.add(new FramebufferTextureSpecification(Framebuffer.FramebufferTextureFormat.RGBA8));
        fbSpec.attachments.attachments.add(new FramebufferTextureSpecification(Framebuffer.FramebufferTextureFormat.RED_INTEGER));
        fbSpec.attachments.attachments.add(new FramebufferTextureSpecification(Framebuffer.FramebufferTextureFormat.Depth));
        fbSpec.width = 1280;
        fbSpec.height = 720;

        framebuffer = Framebuffer.create(fbSpec);

        editorScene = new Scene();
        activeScene = editorScene;

        ApplicationCommandLineArgs commandLineArgs = Application.getApplication().getSpecification().commandLineArgs;

        if (commandLineArgs.count > 1) {
            String sceneFilePath = commandLineArgs.get(0);
            SceneSerializer serializer = new SceneSerializer(activeScene);
            serializer.deserialize(Path.of(sceneFilePath));
        }

        editorCamera = new EditorCamera(30.0f, 1.778f, 0.1f, 1000.0f);

        Renderer2D.setLineWidth(4.0f);
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

        editorCamera.onUpdate(ts);

        Renderer2D.resetStats();
        framebuffer.bind();
        RenderCommand.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
        RenderCommand.clear();

        framebuffer.clearAttachment(1, -1);

        switch (sceneState) {
            case Edit -> {
                if (viewportFocused) {
                    cameraController.onUpdate(ts);
                }
                editorCamera.onUpdate(ts);
                activeScene.onUpdateEditor(ts, editorCamera);
            }
            case Simulate -> {
                editorCamera.onUpdate(ts);
                activeScene.onUpdateSimulation(ts, editorCamera);
            }
            case Play -> activeScene.onUpdateRuntime(ts);
        }

        ImVec2 mousePosition = ImGui.getMousePos();
        mousePosition.x -= viewportBounds[0].x;
        mousePosition.y -= viewportBounds[0].y;
        Vector2f viewportSize = viewportBounds[1].sub(viewportBounds[0], new Vector2f());
        mousePosition.y = viewportSize.y - mousePosition.y;

        int mouseX = (int) mousePosition.x;
        int mouseY = (int) mousePosition.y;

        if (mouseX >= 0 && mouseY >= 0 && mouseX < (int) viewportSize.x && mouseY < (int) viewportSize.y) {
            int pixelData = framebuffer.readPixel(1, mouseX, mouseY);
            hoveredEntity = activeScene.getEntityById(pixelData);
        }

        onOverlayRender();

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
            ImGui.setNextWindowSize(viewport.getSizeX(), viewport.getSizeY());
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

                if (ImGui.menuItem("Save...", "Ctrl+S")) {
                    saveScene();
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
        contentBrowserPanel.onImGuiRender();

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

        ImGui.begin("Settings");
        if (ImGui.checkbox("Show Physics Colliders", showPhysicsColliders)) {
            showPhysicsColliders = !showPhysicsColliders;
        }
        ImGui.end();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin("Viewport");

        ImVec2 viewportMinRegion = ImGui.getWindowContentRegionMin();
        ImVec2 viewportMaxRegion = ImGui.getWindowContentRegionMax();
        ImVec2 viewportOffset = ImGui.getWindowPos();

        viewportBounds[0] = new Vector2f(viewportMinRegion.x + viewportOffset.x, viewportMinRegion.y + viewportOffset.y);
        viewportBounds[1] = new Vector2f(viewportMaxRegion.x + viewportOffset.x, viewportMaxRegion.y + viewportOffset.y);

        viewportFocused = ImGui.isWindowFocused();
        viewportHovered = ImGui.isWindowHovered();

        Application.getApplication().getImGuiLayer().blockEvents(!viewportFocused && !viewportHovered);

        ImVec2 viewportPanelSize = ImGui.getContentRegionAvail();

        viewportSize.x = viewportPanelSize.x;
        viewportSize.y = viewportPanelSize.y;

        int textureId = framebuffer.getColorAttachmentRendererId();
        ImGui.image(textureId, viewportSize.x, viewportSize.y, 0, 1, 1, 0);

        if (ImGui.beginDragDropTarget()) {
            ImGuiPayload<?> payload = ImGui.acceptDragDropPayload("CONTENT_BROWSER_ITEM");
            if (payload != null) {
                openScene(ContentBrowserPanel.assetPath.resolve(String.valueOf(payload.getData())));
            }
            ImGui.endDragDropTarget();
        }

        Entity selectedEntity = sceneHierarchyPanel.getSelectedEntity();
        if (selectedEntity != null && gizmoType != -1) {
            ImGuizmo.setOrthographic(false);
            ImGuizmo.setDrawList();

            ImGuizmo.setRect(viewportBounds[0].x, viewportBounds[0].y, viewportBounds[1].x - viewportBounds[0].x, viewportBounds[1].y - viewportBounds[0].y);

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

        uiToolbar();

        ImGui.end();
    }

    public void uiToolbar() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 2);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);

        float[][] colors = ImGui.getStyle().getColors();

        float[] buttonHovered = colors[ImGuiCol.ButtonHovered];
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, buttonHovered[0], buttonHovered[1], buttonHovered[2], 0.5f);
        float[] buttonActive = colors[ImGuiCol.ButtonActive];
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, buttonActive[0], buttonActive[1], buttonActive[2], 0.5f);

        ImGui.begin("##toolbar", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        boolean toolbarEnabled = activeScene != null;

        Vector4f tintColor = new Vector4f(1, 1, 1, 1);

        if (!toolbarEnabled) {
            tintColor.w = 0.5f;
        }

        float size = ImGui.getWindowHeight() - 4.0f;
        Texture2D editSimulateIcon = (sceneState == SceneState.Edit || sceneState == SceneState.Simulate) ? iconPlay : iconStop;
        ImGui.setCursorPosX((ImGui.getWindowContentRegionMax().x * 0.5f) - (size * 0.5f));

        if (ImGui.imageButton(editSimulateIcon.getRendererId(), size, size, 0, 0, 1, 1, 0, 0.0f, 0.0f, 0.0f, 0.0f, tintColor.x, tintColor.y, tintColor.z, tintColor.w) && toolbarEnabled) {
            if (sceneState == SceneState.Edit || sceneState == SceneState.Simulate) {
                onScenePlay();
            } else if (sceneState == SceneState.Play) {
                onSceneStop();
            }
        }

        ImGui.sameLine();

        Texture2D editPlayIcon = (sceneState == SceneState.Edit || sceneState == SceneState.Play) ? iconSimulate : iconStop;
        if (ImGui.imageButton(editPlayIcon.getRendererId(), size, size, 0, 0, 1, 1, 0, 0.0f, 0.0f, 0.0f, 0.0f, tintColor.x, tintColor.y, tintColor.z, tintColor.w) && toolbarEnabled) {
            if (sceneState == SceneState.Edit || sceneState == SceneState.Play) {
                onSimulatePlay();
            } else if (sceneState == SceneState.Simulate) {
                onSceneStop();
            }
        }

        ImGui.popStyleVar(2);
        ImGui.popStyleColor(3);
        ImGui.end();
    }

    @Override
    public void onEvent(Event event) {
        cameraController.onEvent(event);

        if (sceneState == SceneState.Edit) {
            editorCamera.onEvent(event);
        }

        EventDispatcher eventDispatcher = new EventDispatcher(event);
        eventDispatcher.dispatch(KeyPressedEvent.class, this::onKeyPressed);
        eventDispatcher.dispatch(MouseButtonPressedEvent.class, this::onMouseButtonPressed);
    }

    private boolean onKeyPressed(KeyPressedEvent event) {
        if (event.isRepeat()) {
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
                if (control) {
                    if (shift) {
                        saveSceneAs();
                    } else {
                        saveScene();
                    }
                    return true;
                }

            }
            case KeyCodes.D -> {
                if (control) {
                    onDuplicateEntity();
                }
            }
            case KeyCodes.Q -> {
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

    private boolean onMouseButtonPressed(MouseButtonPressedEvent event) {
        if (event.getMouseButton() == MouseButtonCodes.BUTTON_LEFT) {
            if (viewportHovered && !ImGuizmo.isOver() && !Input.isKeyPressed(KeyCodes.LEFT_ALT)) {
                sceneHierarchyPanel.setSelectedEntity(hoveredEntity);
            }
        }
        return false;
    }

    private void onOverlayRender() {
        if (sceneState == SceneState.Play) {
            Entity camera = activeScene.getPrimaryCameraEntity();

            if (camera == null) {
                return;
            }

            Renderer2D.beginScene(camera.getComponent(CameraComponent.class).camera, camera.getComponent(TransformComponent.class).getTransform());
        } else {
            Renderer2D.beginScene(editorCamera);
        }

        if (showPhysicsColliders) {
            activeScene.getRegistry().findEntitiesWith(TransformComponent.class, BoxCollider2DComponent.class)
                    .stream().forEach(component -> {
                        TransformComponent tc = component.comp1();
                        BoxCollider2DComponent bc2dc = component.comp2();

                        Vector3f translation = new Vector3f(tc.translation).add(new Vector3f(bc2dc.offset, 0.001f));
                        Vector3f scale = new Vector3f(tc.scale).mul(new Vector3f(new Vector2f(bc2dc.size).mul(2.0f), 1.0f));

                        Matrix4f rotation = new Matrix4f().rotate(new Quaternionf().rotateZ(tc.rotation.z));
                        Matrix4f transform = new Matrix4f().translate(translation).mul(rotation).mul(new Matrix4f().scale(scale));

                        Renderer2D.drawRect(transform, new Vector4f(0, 1, 0, 1));
                    });

            activeScene.getRegistry().findEntitiesWith(TransformComponent.class, CircleCollider2DComponent.class)
                    .stream().forEach(component -> {
                        TransformComponent tc = component.comp1();
                        CircleCollider2DComponent cc2dc = component.comp2();

                        Vector3f translation = new Vector3f(tc.translation).add(new Vector3f(cc2dc.offset, 0.001f));
                        Vector3f scale = new Vector3f(tc.scale).mul(new Vector3f(cc2dc.radius * 2.0f));

                        Matrix4f transform = new Matrix4f().translate(translation).mul(new Matrix4f().scale(scale));

                        Renderer2D.drawCircle(transform, new Vector4f(0, 1, 0, 1), 0.01f);
                    });
        }

        Entity selectedEntity = sceneHierarchyPanel.getSelectedEntity();
        if (selectedEntity != null) {
            TransformComponent transformComponent = selectedEntity.getComponent(TransformComponent.class);
            Renderer2D.drawRect(transformComponent.getTransform(), new Vector4f(0.5f, 0.0f, 1.0f, 1.0f));
        }

        Renderer2D.endScene();
    }

    private void newScene() {
        activeScene = new Scene();
        activeScene.onViewportResize((int) viewportSize.x, (int) viewportSize.y);
        sceneHierarchyPanel.setContext(activeScene);
    }

    private void openScene() {
        Optional<String> filePath = WindowsPlatformUtils.openFile("Scene (*.scene)\0*.scene\0");
        filePath.ifPresent(path -> openScene(Path.of(path)));
    }

    private void openScene(Path filePath) {
        if (sceneState != SceneState.Edit) {
            onSceneStop();
        }

        if (!filePath.getFileName().toString().endsWith(".scene")) {
            Log.warn(String.format("Could not load %s - not a scene file", filePath.getFileName()));
            return;
        }

        Scene newScene = new Scene();
        SceneSerializer serializer = new SceneSerializer(newScene);

        if (serializer.deserialize(filePath)) {
            editorScene = newScene;
            editorScene.onViewportResize((int) viewportSize.x, (int) viewportSize.y);
            sceneHierarchyPanel.setContext(editorScene);

            activeScene = editorScene;
            editorScenePath = filePath;
        }
    }

    private void saveScene() {
        if (editorScenePath != null) {
            serializeScene(activeScene, editorScenePath);
        } else {
            saveSceneAs();
        }
    }

    private void saveSceneAs() {
        Optional<String> filePath = WindowsPlatformUtils.saveFile("Scene (*.scene)\0*.scene\0");

        if (filePath.isPresent()) {
            Path path = Path.of(filePath.get());
            serializeScene(activeScene, path);
            editorScenePath = path;
        }
    }

    private void serializeScene(Scene scene, Path filePath) {
        SceneSerializer serializer = new SceneSerializer(scene);
        serializer.serialize(filePath);
    }

    private void onScenePlay() {
        if (sceneState == SceneState.Simulate) {
            onSceneStop();
        }

        sceneState = SceneState.Play;
        activeScene = Scene.copy(editorScene);
        activeScene.onRuntimeStart();
        sceneHierarchyPanel.setContext(activeScene);
    }

    private void onSimulatePlay() {
        if (sceneState == SceneState.Play) {
            onSceneStop();
        }

        sceneState = SceneState.Simulate;
        activeScene = Scene.copy(editorScene);
        activeScene.onSimulationStart();
        sceneHierarchyPanel.setContext(activeScene);
    }

    private void onSceneStop() {
        //Some sort of exception HZ_CORE_ASSERT(m_SceneState == SceneState::Play || m_SceneState == SceneState::Simulate);
        if (sceneState == SceneState.Play) {
            activeScene.onRuntimeStop();
        } else if (sceneState == SceneState.Simulate) {
            activeScene.onSimulationStop();
        }

        sceneState = SceneState.Edit;

        activeScene = editorScene;
        sceneHierarchyPanel.setContext(activeScene);
    }

    private void onDuplicateEntity() {
        if (sceneState != SceneState.Edit) {
            return;
        }
        Entity selectedEntity = sceneHierarchyPanel.getSelectedEntity();
        if (selectedEntity != null) {
            editorScene.duplicateEntity(selectedEntity);
        }
    }
}
