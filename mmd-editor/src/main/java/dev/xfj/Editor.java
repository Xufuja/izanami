package dev.xfj;

import dev.xfj.engine.Application;
import dev.xfj.engine.Log;

import java.io.IOException;

public class Editor extends Application {

    public Editor() throws IOException {
        pushLayer(new ExampleLayer());
        Log.client().info("Layer pushed!");
    }
}
