package dev.xfj.engine.imgui;

import dev.xfj.engine.core.Application;
import dev.xfj.engine.core.Layer;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.event.Event;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImGuiLayer extends Layer {

    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    public ImGuiLayer() {
        super("ImGuiLayer");
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
        //io.setConfigViewportsNoTaskBarIcon(true);
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
    }

    @Override
    public void onDetach() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    public void begin() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void end() {
        final ImGuiIO io = ImGui.getIO();
        Application application = Application.getApplication();
        io.setDisplaySize(application.getWindow().getWidth(), application.getWindow().getHeight());
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
    public void onUpdate(TimeStep ts) {

    }

    @Override
    public void onEvent(Event event) {
    }
}

