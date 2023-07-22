package dev.xfj.engine.scripting;

import dev.xfj.engine.core.UUID;
import dev.xfj.engine.scene.Scene;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.util.HashMap;
import java.util.Map;

public class ScriptEngineData {
    public Context rootDomain = null;
    public Source coreAssembly = null;
    public Source appAssembly = null;
    public ScriptClass entityClass = null;
    public Map<String, ScriptClass> entityClasses = new HashMap<>();
    public Map<UUID, ScriptInstance> entityInstances =  new HashMap<>();
    public Map<UUID, Map<String, ScriptFieldInstance>> entityScriptFields = new HashMap<>();
    public Scene sceneContext = null;

}
