package dev.xfj.engine.scene;


import dev.xfj.protobuf.Scene;

public class SceneSerializer {
    private final Scene scene;

    public SceneSerializer(Scene scene) {
        this.scene = scene;
    }

    public void serialize(String filepath) {
        Scene.Builder sceneBuilder = Scene.newBuilder();

    }

    public void serializeRuntime(String filepath) {

    }

    public void deserialize(String filepath) {

    }

    public void deserializeRuntime(String filepath) {

    }
}
