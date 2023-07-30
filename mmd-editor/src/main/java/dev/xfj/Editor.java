package dev.xfj;

import dev.xfj.engine.core.application.Application;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.application.ApplicationCommandLineArgs;
import dev.xfj.engine.core.application.ApplicationSpecification;
import dev.xfj.engine.project.Project;

import java.nio.file.Path;

public class Editor extends Application {
    public static Application createApplication(ApplicationCommandLineArgs args) {
        ApplicationSpecification spec = new ApplicationSpecification();
        spec.name = "MMD Editor";
        spec.commandLineArgs = args;

        return new Editor(spec);
    }

    public Editor(ApplicationSpecification specification) {
        super(specification);
        pushLayer(new EditorLayer());
        Log.client().info("Layer pushed!");
    }
}
