package dev.xfj;

import dev.xfj.engine.core.application.Application;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.application.ApplicationCommandLineArgs;

public class Sandbox extends Application {
    public static Application createApplication(ApplicationCommandLineArgs args) {
        return new Sandbox(args);
    }

    public Sandbox(ApplicationCommandLineArgs args) {
        super("Sandbox", args);
        //pushLayer(new ExampleLayer());
        pushLayer(new Sandbox2D());
        Log.client().info("Layer pushed!");
    }
}
