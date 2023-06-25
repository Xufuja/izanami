package dev.xfj.engine.scripting;

import dev.xfj.engine.scene.Entity;
import org.graalvm.polyglot.Value;

public class ScriptInstance {
    private ScriptClass scriptClass;
    private Value instance;
    private String constructor;
    private String onCreateMethod;
    private String onUpdateMethod;

    public ScriptInstance(ScriptClass scriptClass, Entity entity) {
        this.scriptClass = scriptClass;
        this.instance = scriptClass.instantiate();
        this.constructor = ScriptEngine.data.entityClass.getMethod("constructor", 0);
        this.onCreateMethod = scriptClass.getMethod("onCreate", 0);
        this.onUpdateMethod = scriptClass.getMethod("onUpdate", 0);

        long entityId = entity.getUUID();

    }
}
