package dev.xfj;

import dev.xfj.core.Application;
import dev.xfj.core.ExampleLayer;
import dev.xfj.core.Log;
import org.slf4j.Logger;

public class Main {
    public static final Logger logger = Log.init(Main.class.getSimpleName());

    public static void main(String[] args) {
        logger.info("Starting application");
        Application application = new Editor();
        application.run();
    }
}