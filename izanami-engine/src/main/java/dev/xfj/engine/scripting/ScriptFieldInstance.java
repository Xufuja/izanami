package dev.xfj.engine.scripting;

public class ScriptFieldInstance {
    public ScriptField field;
    protected Object buffer;

    public <T> T getValue(Class<T> type) {
        return type.cast(buffer);
    }

    public void setValue(Object value) {
        buffer = value;
    }

}
