package dev.xfj;

import dev.xfj.core.Application;
import dev.xfj.core.ExampleLayer;
import dev.xfj.core.Log;

public class Editor extends Application {

    public Editor() {
        pushLayer(new ExampleLayer());
        Log.info("Layer pushed!");
    }
}
