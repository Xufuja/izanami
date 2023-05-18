package dev.xfj;

import dev.xfj.engine.core.application.Application;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.application.ApplicationCommandLineArgs;

public class Main {
    public static void main(String[] args) {
        Log.init();
        Log.info("Loggers created");
        Application application = Editor.createApplication(new ApplicationCommandLineArgs(args.length, args));
        application.run();
    }
}