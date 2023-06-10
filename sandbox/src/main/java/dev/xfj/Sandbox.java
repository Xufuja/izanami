package dev.xfj;

import dev.xfj.engine.core.application.Application;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.application.ApplicationCommandLineArgs;
import dev.xfj.engine.core.application.ApplicationSpecification;

import java.io.File;

public class Sandbox extends Application {
    public static Application createApplication(ApplicationCommandLineArgs args) {
        ApplicationSpecification spec = new ApplicationSpecification();
        spec.name = "Sandbox";
        spec.workingDirectory = new File(System.getProperty("user.dir")).getParentFile().toPath().resolve("mmd-editor").toString();
        spec.commandLineArgs = args;
        return new Sandbox(spec);
    }

    public Sandbox(ApplicationSpecification specification) {
        super(specification);
        //pushLayer(new ExampleLayer());
        pushLayer(new Sandbox2D());
        Log.client().info("Layer pushed!");
    }
}
