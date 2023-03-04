package dev.xfj.window;

import dev.xfj.Log;
import dev.xfj.events.application.WindowCloseEvent;
import dev.xfj.events.application.WindowResizeEvent;
import dev.xfj.events.key.KeyPressedEvent;
import dev.xfj.events.key.KeyReleasedEvent;
import dev.xfj.events.mouse.MouseButtonPressedEvent;
import dev.xfj.events.mouse.MouseButtonReleasedEvent;
import dev.xfj.events.mouse.MouseMovedEvent;
import dev.xfj.events.mouse.MouseScrolledEvent;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public static final Logger logger = Log.init(Window.class.getSimpleName());
    public static boolean glfwInitialized = false;
    private long window;
    private final WindowData windowData;

    public static Window create() {
        WindowProps windowProps = new WindowProps();
        return new Window(windowProps);
    }

    private Window(WindowProps windowProps) {
        this.windowData = new WindowData();
        init(windowProps);
    }

    public void init(WindowProps windowProps) {
        windowData.title = windowProps.title;
        windowData.width = windowProps.width;
        windowData.height = windowProps.height;

        logger.info(String.format("Creating new window %1$s (%2$d, %3$d)", windowProps.title, windowProps.width, windowProps.height));

        if (!glfwInitialized) {
            boolean success = glfwInit();
            if (!success) {
                throw new RuntimeException("Could not initialize GLFW!");
            } else {
                glfwSetErrorCallback(new GLFWErrorCallback() {
                    @Override
                    public void invoke(int error, long description) {
                        logger.error(String.format("GLFW error (%1$d): %2$d", error, description));
                    }
                });
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
                switch (action) {
                    case GLFW_PRESS -> {
                        KeyPressedEvent event = new KeyPressedEvent(key, 0);
                        windowData.eventCallback.handle(event);
                    }
                    case GLFW_RELEASE -> {
                        KeyReleasedEvent event = new KeyReleasedEvent(key);
                        windowData.eventCallback.handle(event);
                    }
                    case GLFW_REPEAT -> {
                        KeyPressedEvent event = new KeyPressedEvent(key, 1);
                        windowData.eventCallback.handle(event);
                    }
                }
            }
        });
        glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                switch (action) {
                    case GLFW_PRESS -> {
                        MouseButtonPressedEvent event = new MouseButtonPressedEvent(button);
                        windowData.eventCallback.handle(event);
                    }
                    case GLFW_RELEASE -> {
                        MouseButtonReleasedEvent event = new MouseButtonReleasedEvent(button);
                        windowData.eventCallback.handle(event);
                    }
                }
            }
        });
        glfwSetScrollCallback(window, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xOffset, double yOffset) {
                MouseScrolledEvent event = new MouseScrolledEvent((float) xOffset, (float) yOffset); //Why the cast?
                windowData.eventCallback.handle(event);
            }
        });
        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPosition, double yPosition) {
                MouseMovedEvent event = new MouseMovedEvent((float) xPosition, (float) yPosition);
                windowData.eventCallback.handle(event);
            }
        });
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
