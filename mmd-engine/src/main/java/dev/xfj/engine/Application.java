package dev.xfj.engine;

import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.event.Event;
import dev.xfj.engine.event.EventDispatcher;
import dev.xfj.engine.event.application.WindowCloseEvent;
import dev.xfj.engine.imgui.ImGuiLayer;
import dev.xfj.engine.renderer.Renderer;
import dev.xfj.engine.window.Window;
import dev.xfj.platform.windows.WindowsInput;
import dev.xfj.platform.windows.WindowsWindow;

import java.util.ListIterator;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Application {
    private static Application application;
    private final Window window;
    private final ImGuiLayer imGuiLayer;
    private boolean running;
    private final LayerStack layerStack;
    private float lastFrameTime;

    static {
        //Not entirely sure how the Singleton is initialized in the C++ version so just sticking it here for now
        //It seems to do the same thing as using this static block, but not sure how that is being called
        Input.setInput(new WindowsInput());
    }

    public Application() {
        if (application == null) {
            application = this;
        } else {
            Log.error("Application already exists!");
        }

        window = WindowsWindow.create();
        running = true;
        layerStack = new LayerStack();
        window.setEventCallback(this::onEvent);

        Renderer.init();

        imGuiLayer = new ImGuiLayer();
        pushOverlay(imGuiLayer);
        lastFrameTime = 0.0f;
    }

    public void run() {
        while (running) {
            float time = (float) glfwGetTime();
            TimeStep timeStep = new TimeStep(time - lastFrameTime);
            lastFrameTime = time;

            for (Layer layer : layerStack.getLayers()) {
                layer.onUpdate(timeStep);
            }
            imGuiLayer.begin();
            for (Layer layer : layerStack.getLayers()) {
                layer.onImGuiRender();
            }
            imGuiLayer.end();
            window.onUpdate();
        }
        window.shutdown();
    }

    public void onEvent(Event event) {
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        boolean result = false;
        if (event instanceof WindowCloseEvent) {
            result = eventDispatcher.dispatch(WindowCloseEvent.class, this::onWindowClose);
        }
        if (result) {
            Log.debug("Dispatched: " + event.getClass().getSimpleName());
        }
        Log.trace(event.toString());
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

    public Window getWindow() {
        return window;
    }

    public static Application getApplication() {
        return application;
    }
}
