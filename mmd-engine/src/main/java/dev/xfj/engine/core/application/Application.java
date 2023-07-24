package dev.xfj.engine.core.application;

import dev.xfj.engine.core.*;
import dev.xfj.engine.core.window.Window;
import dev.xfj.engine.core.window.WindowProps;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.events.EventDispatcher;
import dev.xfj.engine.events.application.WindowCloseEvent;
import dev.xfj.engine.events.application.WindowResizeEvent;
import dev.xfj.engine.imgui.ImGuiLayer;
import dev.xfj.engine.renderer.Renderer;
import dev.xfj.engine.scripting.ScriptEngine;
import dev.xfj.engine.utils.PlatformUtils;
import dev.xfj.platform.windows.WindowsPlatformUtils;

import java.util.ListIterator;

public class Application {
    private static Application instance;
    private final ApplicationSpecification specification;
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
        PlatformUtils.setPlatformUtils(PlatformUtils.create());
    }

    public Application(ApplicationSpecification specification) {
        this.running = true;
        this.minimized = false;
        this.layerStack = new LayerStack();
        this.lastFrameTime = 0.0f;

        if (instance == null) {
            instance = this;
        } else {
            //Some sort of exception HZ_CORE_ASSERT(!s_Instance, "Application already exists!");
            Log.error("Application already exists!");
        }

        this.specification = specification;

        if (!specification.workingDirectory.isEmpty() && !specification.workingDirectory.isBlank()) {

            System.setProperty("user.dir", specification.workingDirectory);
        }

        this.window = Window.create(new WindowProps(specification.name));
        this.window.setEventCallback(this::onEvent);

        Renderer.init();
        ScriptEngine.init();

        this.imGuiLayer = new ImGuiLayer();
        pushOverlay(imGuiLayer);
    }

    public void run() {
        while (running) {
            float time = PlatformUtils.getTime();
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
        ScriptEngine.shutdown();
        Renderer.shutdown();
    }

    public void close() {
        this.running = false;
    }

    public ImGuiLayer getImGuiLayer() {
        return imGuiLayer;
    }

    public void onEvent(Event event) {
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        eventDispatcher.dispatch(WindowCloseEvent.class, this::onWindowClose);
        eventDispatcher.dispatch(WindowResizeEvent.class, this::onWindowResize);

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
        return instance;
    }

    public ApplicationSpecification getSpecification() {
        return specification;
    }
}
