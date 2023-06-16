package dev.xfj.engine.scripting;

import org.graalvm.polyglot.Context;

public class ScriptEngineData {
    public Context rootDomain = null;
    public String coreAssembly = null;
    public ScriptClass entityClass = null;
}
