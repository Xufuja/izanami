package dev.xfj.core;

import dev.xfj.core.events.Event;
import dev.xfj.core.events.EventDispatcher;
import dev.xfj.core.events.application.WindowCloseEvent;
import dev.xfj.core.window.Window;
import dev.xfj.platform.windows.WindowsWindow;
import org.slf4j.Logger;

import java.util.ListIterator;

import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL41.*;

public class Application {
    public static final Logger logger = Log.init(Application.class.getSimpleName());
    private static Application application = null;
    private final Window window;
    private boolean running;
    private final LayerStack layerStack;

    public Application() {
        if (application == null) {
            application = this;
        } else {
            logger.error("Application already exists!");
        }
        window = WindowsWindow.create();
        running = true;
        layerStack = new LayerStack();
        window.setEventCallback(this::onEvent);
    }

    public void run() {
        while (running) {
            glClearColor(1, 0, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);
            for (Layer layer : layerStack.getLayers()) {
                layer.onUpdate();
            }
            window.onUpdate();
        }
        window.shutdown();
        glfwTerminate();
    }

    public void onEvent(Event event) {
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        boolean result = false;
        if (event instanceof WindowCloseEvent) {
            result = eventDispatcher.dispatch(WindowCloseEvent.class, this::onWindowClose);
        }
        if (result) {
            logger.debug("Dispatched: " + event.getClass().getSimpleName());
        }
        logger.trace(event.toString());
        ListIterator<Layer> it = layerStack.getLayers().listIterator(layerStack.getLayers().size());
        while (it.hasPrevious()) {
            Layer layer = it.previous();
            layer.onEvent(event);
            if (event.isHandled()) {
                break;
            }
        }
    }

    public void pushLayer(Layer layer) {
        layerStack.pushLayer(layer);
        layer.onAttach();
    }

    public void pushOverlay(Layer layer) {
        layerStack.pushOverlay(layer);
        layer.onAttach();
    }

    private boolean onWindowClose(WindowCloseEvent windowCloseEvent) {
        running = false;
        return true;
    }

    public Window getWindow() {
        return window;
    }

    public static Application getApplication() {
        return application;
    }
}
