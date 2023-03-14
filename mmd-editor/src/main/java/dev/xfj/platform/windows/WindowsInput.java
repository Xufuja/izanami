package dev.xfj.platform.windows;

import dev.xfj.engine.Application;
import dev.xfj.engine.Input;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;
import java.util.AbstractMap;

import static org.lwjgl.glfw.GLFW.*;

public class WindowsInput extends Input {

    @Override
    protected boolean isKeyPressedImpl(int keyCode) {
        long window = Application.getApplication().getWindow().getNativeWindow();
        int state = glfwGetKey(window, keyCode);
        return state == GLFW_PRESS || state == GLFW_REPEAT;
    }

    @Override
    protected boolean isMouseButtonPressedImpl(int button) {
        long window = Application.getApplication().getWindow().getNativeWindow();
        int state = glfwGetMouseButton(window, button);
        return state == GLFW_PRESS;
    }

    @Override
    protected AbstractMap.SimpleEntry<Float, Float> getMousePositionImpl() {
        long window = Application.getApplication().getWindow().getNativeWindow();
        DoubleBuffer xPosition = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yPosition = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, xPosition, yPosition);
        return new AbstractMap.SimpleEntry<>((float) xPosition.get(0), (float) yPosition.get(0));
    }

    @Override
    protected float getMouseXImpl() {
        return getMousePositionImpl().getKey();
    }

    @Override
    protected float getMouseYImpl() {
        return getMousePositionImpl().getValue();
    }
}
