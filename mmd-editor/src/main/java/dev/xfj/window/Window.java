package dev.xfj.window;

import dev.xfj.events.application.WindowCloseEvent;
import dev.xfj.events.application.WindowResizeEvent;
import dev.xfj.events.key.KeyPressedEvent;
import dev.xfj.events.key.KeyReleasedEvent;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public static boolean glfwInitialized = false;

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
                GLFWErrorCallback.createPrint(System.err).set();
                glfwInitialized = true;
            }
        }
        window = glfwCreateWindow(windowProps.width, windowProps.height, windowProps.title, NULL, NULL);

        glfwMakeContextCurrent(window);
        GL.createCapabilities(); //Special method since the C++ version works differently
        //glfwSetWindowUserPointer(window, windowData);
        setVSync(true);


        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                windowData.width = width;
                windowData.height = height;
                WindowResizeEvent event = new WindowResizeEvent(width, height);
                windowData.eventCallback.handle(event);
            }
        });
        glfwSetWindowCloseCallback(window, new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                WindowCloseEvent event = new WindowCloseEvent();
                windowData.eventCallback.handle(event);
            }
        });
        glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
                    @Override
                    public void invoke(long window, int width, int height) {
                        windowData.width = width;
                        windowData.height = height;
                        WindowResizeEvent event = new WindowResizeEvent(width, height);
                        windowData.eventCallback.handle(event);
                    }
                });
                glfwSetWindowCloseCallback(window, new GLFWWindowCloseCallback() {
                    @Override
                    public void invoke(long window) {
                        WindowCloseEvent event = new WindowCloseEvent();
                        windowData.eventCallback.handle(event);
                    }
                });
                glfwSetKeyCallback(window, new GLFWKeyCallback() {
                    @Override
                    public void invoke(long window, int key, int scanCode, int action, int mods) {
                    }
                });

            }
        });
        //glfwSetMouseButtonCallback
        //glfwSetScrollCallback
        //glfwSetCursorPosCallback
    }

    public void shutdown() {
        glfwDestroyWindow(window);
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

    public long getWindow() {
        return window;
    }

    public WindowData getWindowData() {
        return windowData;
    }
}
