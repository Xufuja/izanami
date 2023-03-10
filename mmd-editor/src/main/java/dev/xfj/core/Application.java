package dev.xfj.core;

import dev.xfj.core.events.Event;
import dev.xfj.core.events.EventDispatcher;
import dev.xfj.core.events.application.WindowCloseEvent;
import dev.xfj.core.imgui.ImGuiLayer;
import dev.xfj.core.window.Window;
import dev.xfj.platform.windows.WindowsInput;
import dev.xfj.platform.windows.WindowsWindow;
import org.slf4j.Logger;

import java.util.AbstractMap;
import java.util.ListIterator;

import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL41.*;

public class Application {
    public static final Logger logger = Log.init(Application.class.getSimpleName());
    private static Application application;
    private final Window window;
    private ImGuiLayer imGuiLayer;
    private boolean running;
    private final LayerStack layerStack;

    static {
        //Not entirely sure how the Singleton is initialized in the C++ version so just sticking it here for now
        //It seems to do the same thing as using this static block, but not sure how that is being called
        Input.setInput(new WindowsInput());
    }

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
        imGuiLayer = new ImGuiLayer();
        pushOverlay(imGuiLayer);
    }

    public void run() {
        while (running) {
            glClearColor(1, 0, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);
            for (Layer layer : layerStack.getLayers()) {
                layer.onUpdate();
            }
            imGuiLayer.begin();
            for (Layer layer : layerStack.getLayers()) {
                layer.onImGuiRender();
            }
            imGuiLayer.end();
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
