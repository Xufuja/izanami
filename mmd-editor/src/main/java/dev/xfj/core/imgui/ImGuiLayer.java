package dev.xfj.core.imgui;

import dev.xfj.core.Application;
import dev.xfj.core.Layer;
import dev.xfj.core.Log;
import dev.xfj.core.events.Event;
import dev.xfj.core.events.EventDispatcher;
import dev.xfj.core.events.application.WindowResizeEvent;
import dev.xfj.core.events.key.KeyPressedEvent;
import dev.xfj.core.events.key.KeyReleasedEvent;
import dev.xfj.core.events.key.KeyTypedEvent;
import dev.xfj.core.events.mouse.MouseButtonPressedEvent;
import dev.xfj.core.events.mouse.MouseButtonReleasedEvent;
import dev.xfj.core.events.mouse.MouseMovedEvent;
import dev.xfj.core.events.mouse.MouseScrolledEvent;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.opengl.GL41;
import org.slf4j.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer extends Layer {
    public static final Logger logger = Log.init(ImGuiLayer.class.getSimpleName());

    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private float time;

    public ImGuiLayer() {
        super("ImGuiLayer");
        time = 0.0f;
    }

    @Override
    public void onAttach() {
        ImGui.createContext();
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        //io.addConfigFlags(ImGuiConfigFlags.NavEnableGamepad);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        // io.setConfigViewportsNoTaskBarIcon(true);
        //io.setConfigViewportsNoAutoMerge(true);
        ImGui.styleColorsDark();

        ImGuiStyle imGuiStyle = ImGui.getStyle();
        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            imGuiStyle.setWindowRounding(0.0f);
            float[][] colors = imGuiStyle.getColors();
            colors[ImGuiCol.WindowBg][3] = 1.0f;
            imGuiStyle.setColors(colors);
        }

        Application application = Application.getApplication();
        long window = application.getWindow().getNativeWindow();
        imGuiGlfw.init(window, true);
        imGuiGl3.init("#version 410");

        /*ImGui.createContext();
        ImGui.styleColorsDark();

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addBackendFlags(ImGuiBackendFlags.HasMouseCursors | ImGuiBackendFlags.HasSetMousePos);

        final int[] keyMap = new int[ImGuiKey.COUNT];
        keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB;
        keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
        keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
        keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP;
        keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
        keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
        keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
        keyMap[ImGuiKey.Home] = GLFW_KEY_HOME;
        keyMap[ImGuiKey.End] = GLFW_KEY_END;
        keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT;
        keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE;
        keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
        keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE;
        keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER;
        keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
        keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
        keyMap[ImGuiKey.A] = GLFW_KEY_A;
        keyMap[ImGuiKey.C] = GLFW_KEY_C;
        keyMap[ImGuiKey.V] = GLFW_KEY_V;
        keyMap[ImGuiKey.X] = GLFW_KEY_X;
        keyMap[ImGuiKey.Y] = GLFW_KEY_Y;
        keyMap[ImGuiKey.Z] = GLFW_KEY_Z;

        io.setKeyMap(keyMap);

        imGuiGl3.init("#version 410");*/
    }

    @Override
    public void onDetach() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    public void begin() {
        final ImGuiIO io = ImGui.getIO();
        Application application = Application.getApplication();
        io.setDisplaySize(application.getWindow().getWidth(), application.getWindow().getHeight());
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void end() {
        final ImGuiIO io = ImGui.getIO();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            long backupCurrentContext = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupCurrentContext);
        }
    }

    @Override
    public void onImGuiRender() {
        ImGui.showDemoWindow();
    }

    @Override
    public void onUpdate() {
        /*final ImGuiIO io = ImGui.getIO();
        Application application = Application.getApplication();
        io.setDisplaySize(application.getWindow().getWidth(), application.getWindow().getHeight());

        float time = (float) glfwGetTime();
        io.setDeltaTime(this.time > 0.0f ? (time - this.time) : (1.0f / 60.0f));
        this.time = time;

        //So the C++ code calls ImGui_ImplOpenGL3_NewFrame(); here, but if you look at it, the only thing it really does is call ImGui_ImplOpenGL3_CreateDeviceObjects
        //In this case, it seems that imGuiGl3.init("#version 410"); in onAttach() actually calls createDeviceObjects() which the the C++ ImGui_ImplOpenGL3_Init("#version 410"); does not do
        //So this is probably fine? It seems to work at least
        ImGui.newFrame();

        ImGui.showDemoWindow();
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());*/
}

    @Override
    public void onEvent(Event event) {
        /*EventDispatcher eventDispatcher = new EventDispatcher(event);
        eventDispatcher.dispatch(MouseButtonPressedEvent.class, this::onMouseButtonPressedEvent);
        eventDispatcher.dispatch(MouseButtonReleasedEvent.class, this::onMouseButtonReleasedEvent);
        eventDispatcher.dispatch(MouseMovedEvent.class, this::onMouseMovedEvent);
        eventDispatcher.dispatch(MouseScrolledEvent.class, this::onMouseScrolledEvent);
        eventDispatcher.dispatch(KeyPressedEvent.class, this::onKeyPressedEvent);
        eventDispatcher.dispatch(KeyReleasedEvent.class, this::onKeyReleasedEvent);
        eventDispatcher.dispatch(KeyTypedEvent.class, this::onKeyTypedEvent);
        eventDispatcher.dispatch(WindowResizeEvent.class, this::onWindowResizeEvent);*/
    }

    /*private boolean onMouseButtonPressedEvent(MouseButtonPressedEvent event) {
        final ImGuiIO io = ImGui.getIO();
        io.setMouseDown(event.getButton(), true);
        return false;
    }

    private boolean onMouseButtonReleasedEvent(MouseButtonReleasedEvent event) {
        final ImGuiIO io = ImGui.getIO();
        io.setMouseDown(event.getButton(), false);
        return false;
    }

    private boolean onMouseMovedEvent(MouseMovedEvent event) {
        final ImGuiIO io = ImGui.getIO();
        io.setMousePos(event.getX(), event.getY());
        return false;
    }

    private boolean onMouseScrolledEvent(MouseScrolledEvent event) {
        final ImGuiIO io = ImGui.getIO();
        io.setMouseWheelH(io.getMouseWheelH() + event.getxOffset());
        io.setMouseWheel(io.getMouseWheel() + event.getyOffset());
        return false;
    }

    private boolean onKeyPressedEvent(KeyPressedEvent event) {
        final ImGuiIO io = ImGui.getIO();
        io.setKeysDown(event.getKeyCode(), true);
        io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
        io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
        io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
        io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
        return false;
    }

    private boolean onKeyReleasedEvent(KeyReleasedEvent event) {
        final ImGuiIO io = ImGui.getIO();
        io.setKeysDown(event.getKeyCode(), false);
        return false;
    }

    private boolean onKeyTypedEvent(KeyTypedEvent event) {
        final ImGuiIO io = ImGui.getIO();
        int keyCode = event.getKeyCode();
        if (keyCode > 0 && keyCode < 0x10000) {
            io.addInputCharacter(keyCode);
        }
        return false;
    }

    private boolean onWindowResizeEvent(WindowResizeEvent event) {
        final ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(event.getWidth(), event.getHeight());
        io.setDisplayFramebufferScale(1.0f, 1.0f);
        GL41.glViewport(0, 0, event.getWidth(), event.getHeight());
        return false;
    }*/
}

