package dev.xfj.engine;

import dev.xfj.engine.event.Event;
import dev.xfj.engine.event.EventDispatcher;
import dev.xfj.engine.event.application.WindowCloseEvent;
import dev.xfj.engine.imgui.ImGuiLayer;
import dev.xfj.engine.renderer.*;
import dev.xfj.engine.renderer.buffer.BufferElement;
import dev.xfj.engine.renderer.buffer.BufferLayout;
import dev.xfj.engine.renderer.buffer.IndexBuffer;
import dev.xfj.engine.renderer.buffer.VertexBuffer;
import dev.xfj.engine.window.Window;
import dev.xfj.platform.windows.WindowsInput;
import dev.xfj.platform.windows.WindowsWindow;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ListIterator;

public class Application {
    private static Application application;

    private final Window window;
    private final ImGuiLayer imGuiLayer;
    private boolean running;
    private final LayerStack layerStack;

    public Shader shader;
    public VertexArray vertexArray;

    public Shader blueShader;
    public VertexArray squareVA;

    public OrthographicCamera camera;

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
        camera = new OrthographicCamera(-1.6f, 1.6f, -0.9f, 0.9f);
        window = WindowsWindow.create();
        running = true;
        layerStack = new LayerStack();
        window.setEventCallback(this::onEvent);
        imGuiLayer = new ImGuiLayer();
        pushOverlay(imGuiLayer);

        vertexArray = VertexArray.create();

        float[] vertices = {
                -0.5f, -0.5f, 0.0f, 0.8f, 0.2f, 0.8f, 1.0f,
                0.5f, -0.5f, 0.0f, 0.2f, 0.3f, 0.8f, 1.0f,
                0.0f, 0.5f, 0.0f, 0.8f, 0.8f, 0.2f, 1.0f
        };

        VertexBuffer vertexBuffer = VertexBuffer.create(vertices);

        BufferLayout bufferLayout = new BufferLayout(
                new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position"),
                new BufferElement(BufferElement.ShaderDataType.Float4, "a_Color")
        );
        vertexBuffer.setLayout(bufferLayout);
        vertexArray.addVertexBuffer(vertexBuffer);

        int[] indices = {0, 1, 2};
        IndexBuffer indexBuffer = IndexBuffer.create(indices, indices.length);
        vertexArray.setIndexBuffer(indexBuffer);

        squareVA = VertexArray.create();

        float[] squareVertices = {
                -0.75f, -0.75f, 0.0f,
                0.75f, -0.75f, 0.0f,
                0.75f, 0.75f, 0.0f,
                -0.75f, 0.75f, 0.0f
        };

        VertexBuffer squareVB = VertexBuffer.create(squareVertices);
        squareVB.setLayout(new BufferLayout(new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position")));
        squareVA.addVertexBuffer(squareVB);

        int[] squareIndices = {0, 1, 2, 2, 3, 0};
        IndexBuffer squareIB = IndexBuffer.create(squareIndices, squareIndices.length);
        squareVA.setIndexBuffer(squareIB);

        String vertexSrc = """
                #version 330 core
                layout(location = 0) in vec3 a_Position;
                layout(location = 1) in vec4 a_Color;
                uniform mat4 u_ViewProjection;
                out vec3 v_Position;
                out vec4 v_Color;
                void main()
                {
                    v_Position = a_Position;
                    v_Color = a_Color;
                    gl_Position =  u_ViewProjection * vec4(a_Position, 1.0);
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

        String blueShaderVertexSrc = """
                #version 330 core
                layout(location = 0) in vec3 a_Position;
                uniform mat4 u_ViewProjection;
                out vec3 v_Position;
                void main()
                {
                    v_Position = a_Position;
                    gl_Position = u_ViewProjection * vec4(a_Position, 1.0);
                }
                """;
        String blueShaderFragmentSrc = """
                #version 330 core
                layout(location = 0) out vec4 color;
                in vec3 v_Position;
                void main()
                {
                    color = vec4(0.2, 0.3, 0.8, 1.0);
                }
                """;
        blueShader = new Shader(blueShaderVertexSrc, blueShaderFragmentSrc);
    }

    public void run() {
        while (running) {
            RenderCommand.setClearColor(new Vector4f(0.1f, 0.1f, 0.1f, 1));
            RenderCommand.clear();

            camera.setPosition(new Vector3f(0.5f, 0.5f, 0.0f));
            camera.setRotation(45.0f);

            Renderer.beginScene(camera);

            Renderer.submit(blueShader, squareVA);
            Renderer.submit(shader, vertexArray);

            Renderer.endScene();

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
