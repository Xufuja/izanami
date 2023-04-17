package dev.xfj;

import dev.xfj.engine.core.Application;
import dev.xfj.engine.core.Log;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Log.init();
        Log.info("Loggers created");
        Application application = Editor.createApplication();
        application.run();
    }
}