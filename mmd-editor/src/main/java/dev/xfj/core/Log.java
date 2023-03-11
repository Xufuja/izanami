package dev.xfj.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.LoggerFactory;

public class Log {
    private static Logger coreLogger;

    private Log() {
        //https://stackoverflow.com/questions/16910955/programmatically-configure-logback-appender
    }

    public static void init() {
        coreLogger = Log.createLogger("Core");
    }

    public static void error(String line) {
        coreLogger.error(line);
    }

    public static void warn(String line) {
        coreLogger.warn(line);
    }

    public static void info(String line) {
        coreLogger.info(line);
    }

    public static void debug(String line) {
        coreLogger.debug(line);
    }

    public static void trace(String line) {
        coreLogger.trace(line);
    }

    public static Logger createLogger(String name) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setPattern("%d{HH:mm:ss} [%thread] %-5level %logger{36}: %msg%n");
        patternLayoutEncoder.setContext(loggerContext);
        patternLayoutEncoder.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setEncoder(patternLayoutEncoder);
        consoleAppender.setContext(loggerContext);
        consoleAppender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(name);
        logger.addAppender(consoleAppender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);

        return logger;
    }
}
