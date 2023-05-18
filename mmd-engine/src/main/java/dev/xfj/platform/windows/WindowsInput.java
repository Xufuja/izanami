package dev.xfj.platform.windows;

import dev.xfj.engine.core.application.Application;
import dev.xfj.engine.core.Input;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;

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
    protected Vector2f getMousePositionImpl() {
        long window = Application.getApplication().getWindow().getNativeWindow();
        DoubleBuffer xPosition = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yPosition = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, xPosition, yPosition);
        return new Vector2f((float) xPosition.get(0), (float) yPosition.get(0));
    }

    @Override
    protected float getMouseXImpl() {
        return getMousePositionImpl().x;
    }

    @Override
    protected float getMouseYImpl() {
        return getMousePositionImpl().y;
    }
}
