package dev.xfj;

import dev.xfj.events.Event;
import dev.xfj.events.EventDispatcher;
import dev.xfj.events.application.WindowCloseEvent;
import dev.xfj.window.Window;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;

import java.util.function.Function;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


public class Application {
    public static final Logger logger = Log.init(Application.class.getSimpleName());
    private boolean running;
    private Window window;

    public Application() {
        running = true;
        window = Window.create();
        window.setEventCallback(this::onEvent);
    }

    public void run() {
        while (running) {
            GL.createCapabilities();
            glClearColor(1, 0, 1, 1);

            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            while (!glfwWindowShouldClose(window.getWindow())) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

                glfwSwapBuffers(window.getWindow()); // swap the color buffers

                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();
            }
        }
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window.getWindow());
        glfwDestroyWindow(window.getWindow());

        // Terminate GLFW and free the error callback
        glfwTerminate();
    }

    public void onEvent(Event event) {
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        Function<WindowCloseEvent, Boolean> onClose = e -> {
            onWindowClose(e);
            return true;
        };
        eventDispatcher.dispatch(onClose);
    }

    private void onWindowClose(WindowCloseEvent windowCloseEvent) {
        running = false;
    }

}
