package dev.xfj;

import dev.xfj.engine.core.Application;
import dev.xfj.engine.core.Layer;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.core.window.Window;
import dev.xfj.engine.events.Event;
import dev.xfj.engine.events.EventDispatcher;
import dev.xfj.engine.events.application.WindowResizeEvent;
import dev.xfj.engine.events.mouse.MouseButtonPressedEvent;
import dev.xfj.engine.renderer.OrthographicCamera;
import dev.xfj.engine.renderer.RenderCommand;
import dev.xfj.engine.renderer.Renderer2D;
import dev.xfj.level.Level;
import imgui.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class GameLayer extends Layer {
    private OrthographicCamera camera;
    private Level level;
    private ImFont font;
    private float time;
    private boolean blink;
    private GameState gameState;

    private enum GameState {
        Play,
        MainMenu,
        GameOver
    }

    public GameLayer() {
        super("GameLayer");
        this.level = new Level();
        this.time = 0.0f;
        this.blink = false;
        this.gameState = GameState.MainMenu;
        Window window = Application.getApplication().getWindow();
        createCamera(window.getWidth(), window.getHeight());
    }

    @Override
    public void onAttach() {
        level.init();
        ImGuiIO io = ImGui.getIO();
        font = io.getFonts().addFontFromFileTTF("assets/OpenSans-Regular.ttf", 120.0f);
        io.getFonts().build();

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onUpdate(TimeStep ts) {
        time += ts.getTime();
        if ((int) (time * 10.0f) % 8 > 4) {
            blink = !blink;
        }
        if (level.isGameOver()) {
            gameState = GameState.GameOver;
        }
        Vector2f playerPos = level.getPlayer().getPosition();
        camera.setPosition(new Vector3f(playerPos.x, playerPos.y, 0.0f));

        switch (gameState) {
            case Play -> level.onUpdate(ts);
        }

        RenderCommand.setClearColor(new Vector4f(0.0f, 0.0f, 0.0f, 1));
        RenderCommand.clear();

        Renderer2D.beginScene(camera);
        level.onRender();
        Renderer2D.endScene();
    }

    @Override
    public void onImGuiRender() {
        switch (gameState) {
            case Play -> {
                int playerScore = level.getPlayer().getScore();
                String score = String.format("Score %1%s", playerScore);
                ImGui.getForegroundDrawList().addText(font, 48.0f, ImGui.getWindowPos().x, ImGui.getWindowPos().y, 0xffffffff, score);
            }
            case MainMenu -> {
                ImVec2 pos = ImGui.getWindowPos();
                int width = Application.getApplication().getWindow().getWidth();
                int height = Application.getApplication().getWindow().getHeight();

                pos.x += width * 0.5f - 300.0f;
                pos.y += 50.0f;

                if (blink) {
                    ImGui.getForegroundDrawList().addText(font, 120.0f, pos.x, pos.y, 0xffffffff, "Click to Play!");
                }
            }
            case GameOver -> {
                ImVec2 pos = ImGui.getWindowPos();
                int width = Application.getApplication().getWindow().getWidth();
                int height = Application.getApplication().getWindow().getHeight();

                pos.x += width * 0.5f - 300.0f;
                pos.y += 50.0f;

                if (blink) {
                    ImGui.getForegroundDrawList().addText(font, 120.0f, pos.x, pos.y, 0xffffffff, "Click to Play!");
                }
                pos.x += 200.0f;
                pos.y += 150.0f;
                int playerScore = level.getPlayer().getScore();
                String score = String.format("Score %1%s", playerScore);
                ImGui.getForegroundDrawList().addText(font, 48.0f, pos.x, pos.y, 0xffffffff, score);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        eventDispatcher.dispatch(WindowResizeEvent.class, this::onWindowResize);
        eventDispatcher.dispatch(MouseButtonPressedEvent.class, this::onMouseButtonPressed);
    }

    public boolean onMouseButtonPressed(MouseButtonPressedEvent event) {
        if (gameState == GameState.GameOver) {
            level.reset();
        }

        gameState = GameState.Play;
        return false;
    }

    public boolean onWindowResize(WindowResizeEvent event) {
        createCamera(event.getWidth(), event.getHeight());
        return false;
    }

    private void createCamera(int width, int height) {
        float aspectRatio = (float) width / (float) height;

        float camWidth = 8.0f;
        float bottom = -camWidth;
        float top = camWidth;
        float left = bottom * aspectRatio;
        float right = top * aspectRatio;
        camera = new OrthographicCamera(left, right, bottom, top);
    }
}
