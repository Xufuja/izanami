package dev.xfj.core;

import dev.xfj.core.events.Event;
import dev.xfj.core.events.EventDispatcher;
import dev.xfj.core.events.application.WindowCloseEvent;
import dev.xfj.core.imgui.ImGuiLayer;
import dev.xfj.core.window.Window;
import dev.xfj.platform.windows.WindowsInput;
import dev.xfj.platform.windows.WindowsWindow;
import org.lwjgl.opengl.GL41;

import java.util.ListIterator;

import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Application {
    private static Application application;

    private final Window window;
    private final ImGuiLayer imGuiLayer;
    private boolean running;
    private final LayerStack layerStack;

    public int vertexArray;
    public int vertexBuffer;
    public int indexBuffer;


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
        imGuiLayer = new ImGuiLayer();
        pushOverlay(imGuiLayer);

        vertexArray = GL41.glGenVertexArrays();
        GL41.glBindVertexArray(vertexArray);

        vertexBuffer = GL41.glGenBuffers();
        GL41.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);

        float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f, 0.5f, 0.0f
        };

        GL41.glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        GL41.glEnableVertexAttribArray(0);
        GL41.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, NULL);

        indexBuffer = GL41.glGenBuffers();
        GL41.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        int[] indices = { 0, 1, 2 };
        GL41.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void run() {
        while (running) {
            GL41.glClearColor(0.1f, 0.1f, 0.1f, 1);
            GL41.glClear(GL_COLOR_BUFFER_BIT);
            GL41.glBindVertexArray(vertexArray);
            GL41.glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0);
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
