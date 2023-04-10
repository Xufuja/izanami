package dev.xfj.engine.imgui;

import dev.xfj.engine.core.Application;
import dev.xfj.engine.core.Layer;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.events.Event;
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
        imGuiGl3.init("#version 450");
    }

    @Override
    public void onDetach() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    public void begin() {
        //Alright, apparently there is a difference between the C++ and Java versions of ImGui
        //imGuiGlfw.init() call createDeviceObjects() which calls updateFontsTexture() to set up the font atlas
        //This happens in onAttach(),
        //The difference is that that C++ equivalent of createDeviceObjects() is called in ImGui_ImplOpenGL3_NewFrame()
        //So in the Java version the font atlas is set up once whereas in C++ it is done on every frame
        //After figuring that out, there is in fact a comment left behind commenting on this in the Java version
        //It mentions that updateFontsTexture() can be separately called to update fonts in runtime
        imGuiGl3.updateFontsTexture();
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
        //ImGui.showDemoWindow();
    }

    @Override
    public void onUpdate(TimeStep ts) {

    }

    @Override
    public void onEvent(Event event) {
        final ImGuiIO io = ImGui.getIO();
        boolean handled = event.isHandled();
        handled |= event.isInCategory(Event.EventCategory.EventCategoryMouse) & io.getWantCaptureMouse();
        handled |= event.isInCategory(Event.EventCategory.EventCategoryKeyboard) & io.getWantCaptureKeyboard();
        event.setHandled(handled);
    }
}

