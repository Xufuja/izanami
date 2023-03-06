package dev.xfj.core.imgui;

import dev.xfj.core.Application;
import dev.xfj.core.Layer;
import dev.xfj.core.Log;
import dev.xfj.core.events.Event;
import dev.xfj.platform.opengl.ImGuiOpenGLRenderer;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiKey;
import org.slf4j.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer extends Layer {
    public static final Logger logger = Log.init(ImGuiLayer.class.getSimpleName());

    private final ImGuiOpenGLRenderer imGuiGl3 = new ImGuiOpenGLRenderer();
    private float time;

    public ImGuiLayer() {
        super("ImGuiLayer");
        time = 0.0f;
    }

    @Override
    public void onAttach() {
        ImGui.createContext();
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

        imGuiGl3.init("#version 410");
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onUpdate() {
        final ImGuiIO io = ImGui.getIO();
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
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    @Override
    public void onEvent(Event event) {
        logger.debug(event.toString());
    }
}
