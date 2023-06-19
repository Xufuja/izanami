package dev.xfj.engine.scripting;

import org.graalvm.polyglot.Value;

import java.util.Objects;

public class ScriptClass {
    private String className;

    public ScriptClass(String className) {
        this.className = className;
    }

    private Value instantiateClass(String javaScriptClass) {
        Value classConstructor = ScriptEngine.data.rootDomain.eval("js", javaScriptClass);
        return classConstructor.newInstance("classInstance");
    }

    public Value instantiate() {
        return instantiateClass(className);
    }

}
