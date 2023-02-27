package dev.xfj;

import org.slf4j.Logger;


public class Application {
    public static final Logger logger = Log.init("Application");

    public Application() {
        logger.info("Example log from {}", Application.class.getSimpleName());
    }


}
