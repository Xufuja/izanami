package dev.xfj;

import dev.xfj.engine.Application;
import dev.xfj.engine.ExampleLayer;
import dev.xfj.engine.Log;

public class Editor extends Application {

    public Editor() {
        pushLayer(new ExampleLayer());
        Log.info("Layer pushed!");
    }
}
