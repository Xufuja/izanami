package dev.xfj.engine.renderer.shader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ShaderLibrary {
    private final Map<String, Shader> shaders;

    public ShaderLibrary() {
        shaders = new HashMap<>();
    }

    public void add(String name, Shader shader) {
        //Some sort of exception if the name is taken
        shaders.put(name, shader);
    }

    public void add(Shader shader) {
        this.add(shader.getName(), shader);
    }

    public Shader load(Path filePath) throws IOException {
        Shader shader = Shader.create(filePath);
        this.add(shader);
        return shader;
    }

    public Shader load(String name, Path filePath) throws IOException {
        Shader shader = Shader.create(filePath);
        this.add(name, shader);
        return shader;
    }

    public Shader get(String name) {
        //Some sort of exception if not found
        return shaders.get(name);
    }

    public boolean exists(String name) {
        return shaders.containsKey(name);
    }
}
