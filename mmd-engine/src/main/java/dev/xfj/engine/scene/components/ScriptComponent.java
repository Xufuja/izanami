package dev.xfj.engine.scene.components;

public class ScriptComponent implements Component {
    public String className;

    public ScriptComponent() {
        this("");
    }

    public ScriptComponent(String className) {
        this.className = className;
    }
}
