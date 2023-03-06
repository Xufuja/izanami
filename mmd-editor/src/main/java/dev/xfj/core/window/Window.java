package dev.xfj.core.window;

public interface Window {
    void onUpdate();
    int getWidth();
    int getHeight();
    void setEventCallback(EventCallBack.EventCallbackFn callback);
    void setVSync(boolean enabled);
    boolean isVSync();
    void shutdown();
}
