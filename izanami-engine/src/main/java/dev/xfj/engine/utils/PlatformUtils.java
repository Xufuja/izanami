package dev.xfj.engine.utils;

import dev.xfj.platform.windows.WindowsPlatformUtils;

import java.util.Optional;

public abstract class PlatformUtils {
    private static PlatformUtils platformUtils;

    public static PlatformUtils create() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new WindowsPlatformUtils();
        } else {
            throw new RuntimeException("Unknown platform!");
        }
    }

    public static Optional<String> openFile(String filter) {
        return platformUtils.openFileImpl(filter);
    }

    public static Optional<String> saveFile(String filter) {
        return platformUtils.saveFileImpl(filter);
    }

    protected abstract Optional<String> openFileImpl(String filter);

    protected abstract Optional<String> saveFileImpl(String filter);

    public static float getTime() {
        return platformUtils.getTimeImpl();
    }

    protected abstract float getTimeImpl();

    public static PlatformUtils getPlatformUtils() {
        return platformUtils;
    }

    public static void setPlatformUtils(PlatformUtils platformUtils) {
        PlatformUtils.platformUtils = platformUtils;
    }
}
