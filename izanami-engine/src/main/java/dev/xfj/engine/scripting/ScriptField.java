package dev.xfj.engine.scripting;

public class ScriptField {
    public ScriptEngine.ScriptFieldType type;
    public String name;

    public ScriptField(ScriptEngine.ScriptFieldType type, String name) {
        this.type = type;
        this.name = name;
    }
}
