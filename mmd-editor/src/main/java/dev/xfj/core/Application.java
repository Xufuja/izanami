package dev.xfj.core;

import dev.xfj.core.event.Event;
import dev.xfj.core.event.EventDispatcher;
import dev.xfj.core.event.application.WindowCloseEvent;
import dev.xfj.core.imgui.ImGuiLayer;
import dev.xfj.core.renderer.Shader;
import dev.xfj.core.renderer.buffer.BufferElement;
import dev.xfj.core.renderer.buffer.BufferLayout;
import dev.xfj.core.renderer.buffer.IndexBuffer;
import dev.xfj.core.renderer.buffer.VertexBuffer;
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
    public Shader shader;
    public VertexBuffer vertexBuffer;
    public IndexBuffer indexBuffer;

    static {
        //Not entirely sure how the Singleton is initialized in the C++ version so just sticking it here for now
        //It seems to do the same thing as using this static block, but not sure how that is being called
        Input.setInput(new WindowsInput());
    }

    public static int shaderDataTypeToOpenGLBaseType(BufferElement.ShaderDataType type) {
        return switch (type) {
            case Float -> GL_FLOAT;
            case Float2 -> GL_FLOAT;
            case Float3 -> GL_FLOAT;
            case Float4 -> GL_FLOAT;
            case Mat3 -> GL_FLOAT;
            case Mat4 -> GL_FLOAT;
            case Int -> GL_INT;
            case Int2 -> GL_INT;
            case Int3 -> GL_INT;
            case Int4 -> GL_INT;
            case Bool -> GL_BOOL;
            default -> {
                Log.error("Unknown ShaderDataType!");
                yield 0;
            }
        };
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

        float[] vertices = {
                -0.5f, -0.5f, 0.0f, 0.8f, 0.2f, 0.8f, 1.0f,
                0.5f, -0.5f, 0.0f, 0.2f, 0.3f, 0.8f, 1.0f,
                0.0f, 0.5f, 0.0f, 0.8f, 0.8f, 0.2f, 1.0f
        };

        vertexBuffer = VertexBuffer.create(vertices);

        BufferLayout bufferLayout = new BufferLayout(
                new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position"),
                new BufferElement(BufferElement.ShaderDataType.Float4, "a_Color")
        );
        vertexBuffer.setLayout(bufferLayout);

        int index = 0;
        BufferLayout layout = vertexBuffer.getLayout();
        for (BufferElement element : layout) {
            GL41.glEnableVertexAttribArray(index);
            GL41.glVertexAttribPointer(index, element.getComponent(), shaderDataTypeToOpenGLBaseType(element.getType()), element.isNormalized(), layout.getStride(), element.offset);
            index++;
        }

        int[] indices = {0, 1, 2};
        indexBuffer = IndexBuffer.create(indices, indices.length);

        String vertexSrc = """
                #version 330 core
                layout(location = 0) in vec3 a_Position;
                layout(location = 1) in vec4 a_Color;
                out vec3 v_Position;
                out vec4 v_Color;
                void main()
                {
                    v_Position = a_Position;
                    v_Color = a_Color;
                    gl_Position = vec4(a_Position, 1.0);
                }
                """;

        String fragmentSrc = """
                #version 330 core
                layout(location = 0) out vec4 color;
                in vec3 v_Position;
                in vec4 v_Color;
                void main()
                {
                    color = vec4(v_Position * 0.5 + 0.5, 1.0);
                    color = v_Color;
                }
                """;

        shader = new Shader(vertexSrc, fragmentSrc);
    }

    public void run() {
        while (running) {
            GL41.glClearColor(0.1f, 0.1f, 0.1f, 1);
            GL41.glClear(GL_COLOR_BUFFER_BIT);
            shader.bind();
            GL41.glBindVertexArray(vertexArray);
            GL41.glDrawElements(GL_TRIANGLES, indexBuffer.getCount(), GL_UNSIGNED_INT, 0);
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
