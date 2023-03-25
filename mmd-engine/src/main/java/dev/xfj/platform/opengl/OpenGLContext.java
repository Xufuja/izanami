package dev.xfj.platform.opengl;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.GraphicsContext;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL45.*;

public class OpenGLContext implements GraphicsContext {
    private final long windowHandle;

    public OpenGLContext(long windowHandle) {
        this.windowHandle = windowHandle;
    }

    @Override
    public void init() {
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities(); //No need for glad, this kind of does the same as gladLoadGLLoader((GLADloadproc)glfwGetProcAddress);
        Log.info("OpenGL Info:");
        Log.info(String.format(" Vendor: %1$s", glGetString(GL_VENDOR)));
        Log.info(String.format(" Renderer: %1$s", glGetString(GL_RENDERER)));
        Log.info(String.format(" Version: %1$s", glGetString(GL_VERSION)));

        IntBuffer versionMajor = BufferUtils.createIntBuffer(1);
        IntBuffer versionMinor = BufferUtils.createIntBuffer(1);
        glGetIntegerv(GL_MAJOR_VERSION, versionMajor);
        glGetIntegerv(GL_MINOR_VERSION, versionMinor);

        if (versionMajor.get(0) <= 4 && (versionMajor.get(0) != 4 || versionMinor.get(0) < 5)) {
            //Also throw some sort of exception
            Log.error("This requires at least OpenGL version 4.5!");
        }
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(windowHandle);
    }
}
