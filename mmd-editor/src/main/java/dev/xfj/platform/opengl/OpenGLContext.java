package dev.xfj.platform.opengl;

import dev.xfj.core.Log;
import dev.xfj.core.renderer.GraphicsContext;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL41.*;

public class OpenGLContext implements GraphicsContext {
    private final long windowHandle;

    public OpenGLContext(long windowHandle) {
        this.windowHandle = windowHandle;
    }

    @Override
    public void init() {
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities(); //No need for glad, this kind of does the same as gladLoadGLLoader((GLADloadproc)glfwGetProcAddress);
        Log.info("OpenGL Info:");
        Log.info(String.format(" Vendor: %1$s", glGetString(GL_VENDOR)));
        Log.info(String.format(" Renderer: %1$s", glGetString(GL_RENDERER)));
        Log.info(String.format(" Version: %1$s", glGetString(GL_VERSION)));
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(windowHandle);
    }
}
