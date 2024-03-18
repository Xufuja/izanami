package dev.xfj.platform.windows;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.window.EventCallBack;
import dev.xfj.engine.core.window.Window;
import dev.xfj.engine.core.window.WindowData;
import dev.xfj.engine.core.window.WindowProps;
import dev.xfj.engine.events.application.WindowCloseEvent;
import dev.xfj.engine.events.application.WindowResizeEvent;
import dev.xfj.engine.events.key.KeyPressedEvent;
import dev.xfj.engine.events.key.KeyReleasedEvent;
import dev.xfj.engine.events.key.KeyTypedEvent;
import dev.xfj.engine.events.mouse.MouseButtonPressedEvent;
import dev.xfj.engine.events.mouse.MouseButtonReleasedEvent;
import dev.xfj.engine.events.mouse.MouseMovedEvent;
import dev.xfj.engine.events.mouse.MouseScrolledEvent;
import dev.xfj.engine.renderer.GraphicsContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowsWindow implements Window {
    public static int glfwWindowCount = 0;
    private long window;
    private GraphicsContext context;
    private final WindowData windowData;


    public WindowsWindow(WindowProps windowProps) {
        this.windowData = new WindowData();
        init(windowProps);
    }

    public void init(WindowProps windowProps) {
        windowData.title = windowProps.title;
        windowData.width = windowProps.width;
        windowData.height = windowProps.height;

        Log.info(String.format("Creating new window %1$s (%2$d, %3$d)", windowProps.title, windowProps.width, windowProps.height));

        if (glfwWindowCount == 0) {
            boolean success = glfwInit();
            if (!success) {
                throw new RuntimeException("Could not initialize GLFW!");
            } else {
                glfwSetErrorCallback(new GLFWErrorCallback() {
                    @Override
                    public void invoke(int error, long description) {
                        Log.error(String.format("GLFW error (%1$d): %2$d", error, description));
                    }
                });
            }
        }

        PointerBuffer monitors = glfwGetMonitors();
        GLFWVidMode videoMode = glfwGetVideoMode(monitors.get(0));

        int[] monitorX = new int[1];
        int[] monitorY = new int[1];
        glfwGetMonitorPos(monitors.get(0), monitorX, monitorY);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        window = glfwCreateWindow(windowProps.width, windowProps.height, windowProps.title, NULL, NULL);
        ++glfwWindowCount;

        glfwDefaultWindowHints();

        glfwSetWindowPos(window, monitorX[0] + (videoMode.width() - windowProps.width) / 2, monitorY[0] + (videoMode.height() - windowProps.height) / 2);
        glfwShowWindow(window);

        context = GraphicsContext.create(window);
        context.init();
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
                        KeyPressedEvent event = new KeyPressedEvent(key);
                        windowData.eventCallback.handle(event);
                    }
                    case GLFW_RELEASE -> {
                        KeyReleasedEvent event = new KeyReleasedEvent(key);
                        windowData.eventCallback.handle(event);
                    }
                    case GLFW_REPEAT -> {
                        KeyPressedEvent event = new KeyPressedEvent(key, true);
                        windowData.eventCallback.handle(event);
                    }
                }
            }
        });
        glfwSetCharCallback(window, new GLFWCharCallback() {
            @Override
            public void invoke(long window, int keyCode) {
                KeyTypedEvent event = new KeyTypedEvent(keyCode);
                windowData.eventCallback.handle(event);
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
        --glfwWindowCount;
        if (glfwWindowCount == 0) {
            glfwTerminate();
        }
    }

    public void onUpdate() {
        glfwPollEvents();
        context.swapBuffers();
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

    public long getNativeWindow() {
        return window;
    }

    public WindowData getWindowData() {
        return windowData;
    }
}
