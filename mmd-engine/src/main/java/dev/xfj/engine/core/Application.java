package dev.xfj.engine.core;

import dev.xfj.engine.core.window.Window;
import dev.xfj.engine.core.window.WindowProps;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.events.EventDispatcher;
import dev.xfj.engine.events.application.WindowCloseEvent;
import dev.xfj.engine.events.application.WindowResizeEvent;
import dev.xfj.engine.imgui.ImGuiLayer;
import dev.xfj.engine.renderer.Renderer;

import java.util.ListIterator;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Application {
    private static Application application;
    private final Window window;
    private final ImGuiLayer imGuiLayer;
    private boolean running;
    private boolean minimized;
    private final LayerStack layerStack;
    private float lastFrameTime;

    static {
        //Not entirely sure how the Singleton is initialized in the C++ version so just sticking it here for now
        //It seems to do the same thing as using this static block, but not sure how that is being called
        Input.setInput(Input.create());
    }

    public Application() {
        this("MMD Application");
    }

    public Application(String name) {
        if (application == null) {
            application = this;
        } else {
            Log.error("Application already exists!");
        }

        window = Window.create(new WindowProps(name));
        running = true;
        minimized = false;
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

            if (!minimized) {
                for (Layer layer : layerStack.getLayers()) {
                    layer.onUpdate(timeStep);
                }
                imGuiLayer.begin();
                for (Layer layer : layerStack.getLayers()) {
                    layer.onImGuiRender();
                }
                imGuiLayer.end();
            }
            window.onUpdate();
        }
        window.shutdown();
        Renderer.shutdown();
    }

    public void close() {
        this.running = false;
    }

    public void onEvent(Event event) {
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        eventDispatcher.dispatch(WindowCloseEvent.class, this::onWindowClose);
        eventDispatcher.dispatch(WindowResizeEvent.class, this::onWindowResize);

        Log.trace(event.toString());

        ListIterator<Layer> it = layerStack.getLayers().listIterator(layerStack.getLayers().size());
        while (it.hasPrevious()) {
            Layer layer = it.previous();
            if (event.isHandled()) {
                break;
            }
            layer.onEvent(event);
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

    private boolean onWindowResize(WindowResizeEvent windowResizeEvent) {
        if (windowResizeEvent.getWidth() == 0 || windowResizeEvent.getHeight() == 0) {
            minimized = true;
            return false;
        }
        minimized = false;
        Renderer.onWindowResize(windowResizeEvent.getWidth(), windowResizeEvent.getHeight());
        return false;
    }

    public Window getWindow() {
        return window;
    }

    public static Application getApplication() {
        return application;
    }
}
