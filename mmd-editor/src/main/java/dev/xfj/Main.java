package dev.xfj;

import dev.xfj.core.Application;
import dev.xfj.core.Log;

public class Main {

    public static void main(String[] args) {
        Log.init();
        Log.info("Loggers created");
        Application application = new Editor();
        application.run();
    }
}