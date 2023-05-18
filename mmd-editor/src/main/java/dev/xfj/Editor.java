package dev.xfj;

import dev.xfj.engine.core.application.Application;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.application.ApplicationCommandLineArgs;

public class Editor extends Application {
    public static Application createApplication(ApplicationCommandLineArgs args) {
        return new Editor(args);
    }

    public Editor(ApplicationCommandLineArgs args) {
        super("MMD Editor", args);
        pushLayer(new EditorLayer());
        Log.client().info("Layer pushed!");
    }
}
