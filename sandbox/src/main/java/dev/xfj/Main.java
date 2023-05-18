package dev.xfj;

import dev.xfj.engine.core.application.Application;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.application.ApplicationCommandLineArgs;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Log.init();
        Log.info("Loggers created");
        Application application = Sandbox.createApplication(new ApplicationCommandLineArgs(args.length, args));
        application.run();
    }
}