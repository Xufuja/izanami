package dev.xfj;

import dev.xfj.core.Application;
import dev.xfj.core.ExampleLayer;
import dev.xfj.core.Log;
import dev.xfj.core.imgui.ImGuiLayer;
import org.slf4j.Logger;

public class Editor extends Application {
    public static final Logger logger = Log.init(Editor.class.getSimpleName());

    public Editor() {
        pushLayer(new ExampleLayer());
        pushOverlay(new ImGuiLayer());
    }
}
