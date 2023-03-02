package dev.xfj.window;

import dev.xfj.events.Event;
import dev.xfj.events.application.WindowResizeEvent;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public static boolean glfwInitialized = false;

    public long getWindow() {
        return window;
    }

    private long window;
    private WindowData windowData;

    public static Window create() {
        WindowProps windowProps = new WindowProps();
        return new Window(windowProps);
    }

    public Window(WindowProps windowProps) {
        this.windowData = new WindowData();
        init(windowProps);
    }

    public void init(WindowProps windowProps) {
        windowData.title = windowProps.title;
        windowData.width = windowProps.width;
        windowData.height = windowProps.height;

        if (!glfwInitialized) {
            boolean success = glfwInit();
            if (!success) {
                throw new RuntimeException("Could not initialize GLFW!");
            } else {
                glfwInitialized = true;
            }
        }
        window = glfwCreateWindow(windowProps.width, windowProps.height, windowProps.title, NULL, NULL);

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> { //From https://www.lwjgl.org/guide
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        glfwMakeContextCurrent(window);
        //glfwSetWindowUserPointer();
        setVSync(true);

    }



    public void onUpdate() {
        glfwPollEvents();
        glfwSwapBuffers(window);
    }

    public int getWidth() {
        return windowData.width;
    }

    public int getHeight() {
        return windowData.height;
    }

    public void setEventCallback(EventCallBack.EventCallbackFn callback) {
        windowData.eventCallback = callback;
    }

    public void setVSync(boolean enabled) {
        if (enabled) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }
        windowData.vSync = enabled;
    }

    public boolean isVSync() {
        return windowData.vSync;
    }
}
