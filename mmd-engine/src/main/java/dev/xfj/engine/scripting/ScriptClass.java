package dev.xfj.engine.scripting;

import dev.xfj.engine.core.Log;
import org.graalvm.polyglot.Value;

public class ScriptClass {
    private final String className;

    public ScriptClass(String className) {
        this.className = className;
    }

    public Value instantiate() {
        return ScriptEngine.instantiateClass(className);
    }

    public String getMethod(String name, int parameterCount) {
        Value clazz = ScriptEngine.data.rootDomain.getBindings("js").getMember(className);
        int arity = 0;

        if (clazz.hasMember("constructor") && name.equals("constructor")) {
            arity = getMethodArity(clazz.getMember(name));
        } else {
            if (clazz.getMember("prototype").hasMember(name)) {
                arity = getMethodArity(clazz.getMember("prototype").getMember(name));
            } else {
                Log.error("Method not found!");
                throw new RuntimeException();
            }
        }

        if (parameterCount == arity) {
            return name;
        } else {
            Log.error(String.format("Method accepting %1$d arguments not found!", parameterCount));
            throw new RuntimeException();
        }
    }

    private int getMethodArity(Value methodValue) {
        String method = String.valueOf(methodValue);
        String arguments = method.substring(method.indexOf("(") + 1,  method.indexOf(")"));

        if (arguments.isBlank() || arguments.isEmpty()) {
            return 0;
        } else {
            return arguments.split(",").length;
        }
    }

    public void invokeMethod(Value instance, String method, Object... params) {
        instance.invokeMember(method, params);
    }

}
