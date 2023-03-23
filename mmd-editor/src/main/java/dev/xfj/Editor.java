package dev.xfj;

import dev.xfj.engine.core.Application;
import dev.xfj.engine.core.Log;

import java.io.IOException;

public class Editor extends Application {

    public Editor() throws IOException {
        pushLayer(new ExampleLayer());
        Log.client().info("Layer pushed!");
    }
}
