package dev.xfj;

import org.slf4j.Logger;

public class Main {
    public static final Logger logger = Log.init("Main");

    public static void main(String[] args) {
        logger.info("Starting application");
        Application application = new Application();
    }
}