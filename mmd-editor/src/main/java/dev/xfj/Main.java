package dev.xfj;

import dev.xfj.engine.Application;
import dev.xfj.engine.Log;

public class Main {

    public static void main(String[] args) {
        Log.init();
        Log.info("Loggers created");
        Application application = new Editor();
        application.run();
    }
}