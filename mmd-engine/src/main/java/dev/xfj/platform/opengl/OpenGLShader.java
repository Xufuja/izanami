package dev.xfj.platform.opengl;

import dev.xfj.engine.Log;
import dev.xfj.engine.renderer.Shader;
import org.joml.*;
import org.lwjgl.opengl.GL45;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class OpenGLShader implements Shader {
    private int renderId;

    public OpenGLShader(String vertexSrc, String fragmentSrc) {
        int vertexShader = GL45.glCreateShader(GL_VERTEX_SHADER);
        GL45.glShaderSource(vertexShader, vertexSrc);
        GL45.glCompileShader(vertexShader);

        int[] isCompiled = new int[1];
        GL45.glGetShaderiv(vertexShader, GL_COMPILE_STATUS, isCompiled);
        if (isCompiled[0] == GL_FALSE) {
            int[] maxLength = new int[]{0};
            GL45.glGetShaderiv(vertexShader, GL_INFO_LOG_LENGTH, maxLength);
            String infoLog = glGetShaderInfoLog(vertexShader, maxLength[0]);
            GL45.glDeleteShader(vertexShader);
            Log.error(infoLog);
            return;
        }
        int fragmentShader = GL45.glCreateShader(GL_FRAGMENT_SHADER);
        GL45.glShaderSource(fragmentShader, fragmentSrc);
        GL45.glCompileShader(fragmentShader);
        GL45.glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, isCompiled);
        if (isCompiled[0] == GL_FALSE) {
            int[] maxLength = new int[]{0};
            GL45.glGetShaderiv(fragmentShader, GL_INFO_LOG_LENGTH, maxLength);
            String infoLog = glGetShaderInfoLog(fragmentShader, maxLength[0]);
            GL45.glDeleteShader(fragmentShader);
            Log.error(infoLog);
            return;
        }
        this.renderId = GL45.glCreateProgram();
        int program = this.renderId;
        GL45.glAttachShader(program, vertexShader);
        GL45.glAttachShader(program, fragmentShader);
        GL45.glLinkProgram(program);

        int[] isLinked = new int[1];
        GL45.glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, isLinked);
        if (isLinked[0] == GL_FALSE) {
            int[] maxLength = new int[]{0};
            GL45.glGetShaderiv(program, GL_INFO_LOG_LENGTH, maxLength);
            String infoLog = glGetShaderInfoLog(program, maxLength[0]);
            GL45.glDeleteProgram(program);
            GL45.glDeleteShader(vertexShader);
            GL45.glDeleteShader(fragmentShader);
            Log.error(infoLog);
            return;
        }
        GL45.glDetachShader(program, vertexShader);
        GL45.glDetachShader(program, fragmentShader);
    }

    @Override
    public void bind() {
        GL45.glUseProgram(this.renderId);
    }

    @Override
    public void unbind() {
        GL45.glUseProgram(0);
    }

    public void uploadUniformInt(String name, int value) {
        int location = GL45.glGetUniformLocation(renderId, name);
        GL45.glUniform1i(location, value);
    }

    public void uploadUniformFloat(String name, float value) {
        int location = GL45.glGetUniformLocation(renderId, name);
        GL45.glUniform1f(location, value);
    }

    public void uploadUniformFloat2(String name, Vector2f value) {
        int location = GL45.glGetUniformLocation(renderId, name);
        GL45.glUniform2f(location, value.x, value.y);
    }

    public void uploadUniformFloat3(String name, Vector3f value) {
        int location = GL45.glGetUniformLocation(renderId, name);
        GL45.glUniform3f(location, value.x, value.y, value.z);
    }

    public void uploadUniformFloat4(String name, Vector4f value) {
        int location = GL45.glGetUniformLocation(renderId, name);
        GL45.glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    public void uploadUniformMat3(String name, Matrix3f matrix) {
        int location = GL45.glGetUniformLocation(renderId, name);
        GL45.glUniformMatrix3fv(location, false, matrix.get(new float[9]));
    }

    public void uploadUniformMat4(String name, Matrix4f matrix) {
        int location = GL45.glGetUniformLocation(renderId, name);
        GL45.glUniformMatrix4fv(location, false, matrix.get(new float[16]));
    }
}
