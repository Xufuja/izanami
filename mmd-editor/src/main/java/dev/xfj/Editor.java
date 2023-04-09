package dev.xfj;

import dev.xfj.engine.core.Application;
import dev.xfj.engine.core.Log;

public class Editor extends Application {
    public static Application createApplication() {
        return new Editor();
    }

    public Editor() {
        super("MMD Editor");
        pushLayer(new EditorLayer());
        Log.client().info("Layer pushed!");
    }
}
