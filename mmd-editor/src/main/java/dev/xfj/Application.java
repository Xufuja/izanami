package dev.xfj;

import dev.xfj.events.Event;
import dev.xfj.events.EventDispatcher;
import dev.xfj.events.application.WindowCloseEvent;
import dev.xfj.window.Window;
import org.slf4j.Logger;

import java.util.ListIterator;

import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.*;

public class Application {
    public static final Logger logger = Log.init(Application.class.getSimpleName());
    private final Window window;
    private boolean running;
    private final LayerStack layerStack;

    public Application() {
        window = Window.create();
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
    }

    public void pushOverlay(Layer layer) {
        layerStack.pushOverlay(layer);
    }

    private boolean onWindowClose(WindowCloseEvent windowCloseEvent) {
        running = false;
        return true;
    }

}
