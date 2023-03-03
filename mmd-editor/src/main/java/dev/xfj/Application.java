package dev.xfj;

import dev.xfj.events.Event;
import dev.xfj.events.EventDispatcher;
import dev.xfj.events.application.WindowCloseEvent;
import dev.xfj.window.Window;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import static org.lwjgl.glfw.GLFW.glfwTerminate;
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
            glClearColor(1, 0, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);
            window.onUpdate();
        }


        // Terminate GLFW and free the error callback
        glfwTerminate();
    }

    public void onEvent(Event event) {
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        eventDispatcher.dispatch(WindowCloseEvent.class, this::onWindowClose);
    }

    private boolean onWindowClose(WindowCloseEvent windowCloseEvent) {
        running = false;
        return true;
    }

}
