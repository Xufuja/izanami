package dev.xfj.engine.scripting;

import org.graalvm.polyglot.Context;

import java.util.HashMap;
import java.util.Map;

public class ScriptEngineData {
    public Context rootDomain = null;
    public String coreAssembly = null;
    public ScriptClass entityClass = null;
    public Map<String, ScriptClass> entityClasses = new HashMap<>();
}
