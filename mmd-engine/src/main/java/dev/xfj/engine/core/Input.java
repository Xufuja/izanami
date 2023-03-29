package dev.xfj.engine.core;

import dev.xfj.platform.windows.WindowsInput;

import java.util.AbstractMap;

public abstract class Input {
    private static Input input;

    static Input create() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new WindowsInput();
        } else {
            throw new RuntimeException("Unknown platform!");
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        return input.isKeyPressedImpl(keyCode);
    }
    public static boolean isMouseButtonPressed(int button) {
        return input.isMouseButtonPressedImpl(button);
    }
    public static AbstractMap.SimpleEntry<Float, Float> getMousePosition() {
        return input.getMousePositionImpl();
    }
    public static float getMouseX(){
        return input.getMouseXImpl();
    }
    public static float getMouseY(){
        return input.getMouseYImpl();
    }
    protected abstract boolean isKeyPressedImpl(int keyCode);
    protected abstract boolean isMouseButtonPressedImpl(int button);
    protected abstract AbstractMap.SimpleEntry<Float, Float> getMousePositionImpl();
    protected abstract float getMouseXImpl();
    protected abstract float getMouseYImpl();

    public static Input getInput() {
        return input;
    }

    public static void setInput(Input input) {
        Input.input = input;
    }
}
