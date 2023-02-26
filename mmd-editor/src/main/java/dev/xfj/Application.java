package dev.xfj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public Application() {
        logger.info("Example log from {}", Application.class.getSimpleName());
    }


}
