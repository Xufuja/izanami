package dev.xfj.engine.utils;

import dev.xfj.platform.windows.WindowsPlatformUtils;

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

    public static String openFile(String filter) {
        return platformUtils.openFileImpl(filter);
    }

    public static String saveFile(String filter) {
        return platformUtils.saveFileImpl(filter);
    }

    protected abstract String openFileImpl(String filter);

    protected abstract String saveFileImpl(String filter);

    public static PlatformUtils getPlatformUtils() {
        return platformUtils;
    }

    public static void setPlatformUtils(PlatformUtils platformUtils) {
        PlatformUtils.platformUtils = platformUtils;
    }
}
