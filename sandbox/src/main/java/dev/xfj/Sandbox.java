package dev.xfj;

import dev.xfj.engine.core.Application;
import dev.xfj.engine.core.Log;

public class Sandbox extends Application {
    public static Application createApplication() {
        return new Sandbox();
    }

    public Sandbox() {
        super("Sandbox");
        //pushLayer(new ExampleLayer());
        pushLayer(new Sandbox2D());
        Log.client().info("Layer pushed!");
    }
}
