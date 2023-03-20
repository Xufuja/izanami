package dev.xfj.platform.opengl;

import dev.xfj.engine.Log;
import dev.xfj.engine.renderer.shader.Shader;
import org.joml.*;
import org.lwjgl.opengl.GL45;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class OpenGLShader implements Shader {
    private int renderId;
    private final String name;

    static int shaderTypeFromString(String type) {
        return switch (type) {
            case "vertex" -> GL_VERTEX_SHADER;
            case "fragment", "pixel" -> GL_FRAGMENT_SHADER;
            default -> {
                Log.error("Unknown Shader Type!");
                yield 0;
            }
        };
    }

    public OpenGLShader(Path filePath) throws IOException {
        String source = readFile(filePath);
        HashMap<Integer, String> shaderSources = preProcess(source);
        compile(shaderSources);

        String fullName = filePath.getFileName().toString();
        int lastDot = fullName.lastIndexOf(".");
        this.name = fullName.substring(0, lastDot);
    }

    public OpenGLShader(String name, String vertexSrc, String fragmentSrc) {
        this.name = name;
        HashMap<Integer, String> sources = new HashMap<>();
        sources.put(GL_VERTEX_SHADER, vertexSrc);
        sources.put(GL_FRAGMENT_SHADER, fragmentSrc);
        compile(sources);
    }

    private String readFile(Path filePath) throws IOException {
        String result;
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            byte[] bytes = inputStream.readAllBytes();
            result = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Log.error("Could not open file: " + filePath);
            throw e;
        }
        return result;
    }

    private HashMap<Integer, String> preProcess(String source) {
        HashMap<Integer, String> shaderSources = new HashMap<>();

        String typeToken = "#type";
        int typeTokenLength = typeToken.length();
        int pos = source.indexOf(typeToken);
        while (pos != -1) {
            int eol = source.indexOf("\r\n", pos);
            //Some sort of exception to handle syntax errors
            int begin = pos + typeTokenLength + 1;
            String type = source.substring(begin, eol);
            //Some sort of exception to handle invalid shader types

            int nextLinePos = source.indexOf("\r\n", eol);
            pos = source.indexOf(typeToken, nextLinePos);
            shaderSources.put(shaderTypeFromString(type), source.substring(nextLinePos, pos == -1 ? source.length() : pos));
        }
        return shaderSources;
    }


    private void compile(HashMap<Integer, String> shaderSources) {
        int program = GL45.glCreateProgram();
        //Some sort of exception if shaderSources has more than 2 entries
        List<Integer> shaderIds = new ArrayList<>(2);
        int glShaderIDIndex = 0;

        for (Map.Entry<Integer, String> entry : shaderSources.entrySet()) {
            int type = entry.getKey();
            String source = entry.getValue();

            int shader = GL45.glCreateShader(type);

            GL45.glShaderSource(shader, source);

            GL45.glCompileShader(shader);

            int[] isCompiled = new int[1];
            GL45.glGetShaderiv(shader, GL_COMPILE_STATUS, isCompiled);
            if (isCompiled[0] == GL_FALSE) {
                int[] maxLength = new int[]{0};
                GL45.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, maxLength);

                String infoLog = glGetShaderInfoLog(shader, maxLength[0]);

                GL45.glDeleteShader(shader);
                Log.error("Shader compilation failure: " + infoLog);
                break;
            }
            GL45.glAttachShader(program, shader);
            shaderIds.add(glShaderIDIndex++, shader);
        }

        this.renderId = program;

        GL45.glLinkProgram(program);

        int[] isLinked = new int[1];
        GL45.glGetProgramiv(program, GL_LINK_STATUS, isLinked);

        if (isLinked[0] == GL_FALSE) {
            int[] maxLength = new int[]{0};
            GL45.glGetProgramiv(program, GL_INFO_LOG_LENGTH, maxLength);

            String infoLog = glGetProgramInfoLog(program, maxLength[0]);

            GL45.glDeleteProgram(program);

            for (int id : shaderIds) {
                GL45.glDeleteShader(id);
            }
            //Should be some sort of exception
            Log.error("Shader link failure " + infoLog);
            return;
        }
        for (int id : shaderIds) {
            GL45.glDetachShader(program, id);
        }
    }

    @Override
    public void bind() {
        GL45.glUseProgram(this.renderId);
    }

    @Override
    public void unbind() {
        GL45.glUseProgram(0);
    }

    @Override
    public String getName() {
        return this.name;
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
