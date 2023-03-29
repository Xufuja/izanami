package dev.xfj.engine.core.window;

import dev.xfj.platform.windows.WindowsWindow;

public interface Window {
    static Window create() {
        String os = System.getProperty("os.name").toLowerCase();
        WindowProps windowProps = new WindowProps();
        if (os.contains("win")) {
            return new WindowsWindow(windowProps);
        } else {
            throw new RuntimeException("Unknown platform!");
        }
    }
    void onUpdate();
    int getWidth();
    int getHeight();
    void setEventCallback(EventCallBack.EventCallbackFn callback);
    void setVSync(boolean enabled);
    boolean isVSync();
    void shutdown();
    long getNativeWindow();
}
