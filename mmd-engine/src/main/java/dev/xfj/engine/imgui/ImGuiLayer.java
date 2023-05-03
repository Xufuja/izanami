package dev.xfj.engine.imgui;

import dev.xfj.engine.core.Application;
import dev.xfj.engine.core.Layer;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.events.Event;
import imgui.*;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImGuiLayer extends Layer {
    //There must be a better way but the Java version does not seem to have an equivalent to io.Fonts->Fonts[0];
    public static ImFont[] fonts;

    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private boolean blockEvents;

    public ImGuiLayer() {
        super("ImGuiLayer");
        blockEvents = true;
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
        fonts = new ImFont[]{
                io.getFonts().addFontFromFileTTF("assets/fonts/opensans/OpenSans-Bold.ttf", 18.0f),
                io.getFonts().addFontFromFileTTF("assets/fonts/opensans/OpenSans-Regular.ttf", 18.0f)
        };
        io.setFontDefault(fonts[1]);

        ImGui.styleColorsDark();

        ImGuiStyle imGuiStyle = ImGui.getStyle();
        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            imGuiStyle.setWindowRounding(0.0f);
            float[][] colors = imGuiStyle.getColors();
            colors[ImGuiCol.WindowBg][3] = 1.0f;
            imGuiStyle.setColors(colors);
        }

        setDarkThemeColors();

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
        ImGuizmo.beginFrame();
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

    public void blockEvents(boolean block) {
        blockEvents = block;
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
        if (blockEvents) {
            final ImGuiIO io = ImGui.getIO();
            boolean handled = event.isHandled();
            handled |= event.isInCategory(Event.EventCategory.EventCategoryMouse) & io.getWantCaptureMouse();
            handled |= event.isInCategory(Event.EventCategory.EventCategoryKeyboard) & io.getWantCaptureKeyboard();
            event.setHandled(handled);
        }
    }

    public void setDarkThemeColors() {
        ImGuiStyle style = ImGui.getStyle();

        float[][] colors = style.getColors();
        colors[ImGuiCol.WindowBg] = new float[]{0.1f, 0.105f, 0.11f, 1.0f};

        colors[ImGuiCol.Header] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.HeaderHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.HeaderActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        colors[ImGuiCol.Button] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.ButtonHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.ButtonActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        colors[ImGuiCol.FrameBg] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.FrameBgHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.FrameBgActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        colors[ImGuiCol.Tab] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TabHovered] = new float[]{0.38f, 0.3805f, 0.381f, 1.0f};
        colors[ImGuiCol.TabActive] = new float[]{0.28f, 0.2805f, 0.281f, 1.0f};
        colors[ImGuiCol.TabUnfocused] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TabUnfocusedActive] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};

        colors[ImGuiCol.TitleBg] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TitleBgActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TitleBgCollapsed] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        style.setColors(colors);
    }
}

