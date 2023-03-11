package dev.xfj.core.renderer;

import dev.xfj.core.Log;
import org.lwjgl.opengl.GL41;

import static org.lwjgl.opengl.GL41.*;

public class Shader {
    private int renderId;

    public Shader(String vertexSrc, String fragmentSrc) {
        int vertexShader = GL41.glCreateShader(GL_VERTEX_SHADER);
        GL41.glShaderSource(vertexShader, vertexSrc);
        GL41.glCompileShader(vertexShader);

        int[] isCompiled = new int[1];
        GL41.glGetShaderiv(vertexShader, GL_COMPILE_STATUS, isCompiled);
        if (isCompiled[0] == GL_FALSE) {
            int[] maxLength = new int[]{0};
            GL41.glGetShaderiv(vertexShader, GL_INFO_LOG_LENGTH, maxLength);
            String infoLog = glGetShaderInfoLog(vertexShader, maxLength[0]);
            GL41.glDeleteShader(vertexShader);
            Log.error(infoLog);
            return;
        }
        int fragmentShader = GL41.glCreateShader(GL_FRAGMENT_SHADER);
        GL41.glShaderSource(fragmentShader, fragmentSrc);
        GL41.glCompileShader(fragmentShader);
        GL41.glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, isCompiled);
        if (isCompiled[0] == GL_FALSE) {
            int[] maxLength = new int[]{0};
            GL41.glGetShaderiv(fragmentShader, GL_INFO_LOG_LENGTH, maxLength);
            String infoLog = glGetShaderInfoLog(fragmentShader, maxLength[0]);
            GL41.glDeleteShader(fragmentShader);
            Log.error(infoLog);
            return;
        }
        this.renderId = GL41.glCreateProgram();
        int program = this.renderId;
        GL41.glAttachShader(program, vertexShader);
        GL41.glAttachShader(program, fragmentShader);
        GL41.glLinkProgram(program);

        int[] isLinked = new int[1];
        GL41.glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, isLinked);
        if (isLinked[0] == GL_FALSE) {
            int[] maxLength = new int[]{0};
            GL41.glGetShaderiv(program, GL_INFO_LOG_LENGTH, maxLength);
            String infoLog = glGetShaderInfoLog(program, maxLength[0]);
            GL41.glDeleteProgram(program);
            GL41.glDeleteShader(vertexShader);
            GL41.glDeleteShader(fragmentShader);
            Log.error(infoLog);
            return;
        }
        GL41.glDetachShader(program, vertexShader);
        GL41.glDetachShader(program, fragmentShader);
    }

    public void bind() {
        GL41.glUseProgram(this.renderId);
    }

    public void unbind() {
        GL41.glUseProgram(0);
    }

}
