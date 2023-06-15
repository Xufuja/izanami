package dev.xfj.engine.scripting;

import dev.xfj.engine.core.Log;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScriptEngine {
    public static ScriptEngineData data = new ScriptEngineData();

    public static void init() {
        initPolyglot();
    }

    public static void shutdown() {
        shutdownPolyglot();
    }

    private static void initPolyglot() {
        try (Context context = Context.newBuilder().option("engine.WarnInterpreterOnly", "false").allowHostAccess(HostAccess.ALL).allowHostClassLookup(className -> true).build()) {
            data.coreAssembly = loadJavaScriptFile("scripts/MMD-ScriptCore.js");
            context.eval("js", data.coreAssembly);

            Value classConstructor = context.eval("js", "Main");
            Value instance = classConstructor.newInstance("mainInstance");

            instance.invokeMember("printMessage");

            instance.invokeMember("printInt", 1);

            int value = 5;
            instance.invokeMember("printInt", value);

            int value2 = 508;
            instance.invokeMember("printInts", value, value2);

            String string = "Hello World from Java!";

            instance.invokeMember("printCustomMessage", string);

            instance.invokeMember("log", "Text passed from Java to JavaScript which calls a Java method");
        }
    }

    private static void shutdownPolyglot() {
        data.coreAssembly = null;
    }

    private static String loadJavaScriptFile(String filePath) {
        ClassLoader classLoader = ScriptEngine.class.getClassLoader();
        String result;
        try (InputStream inputStream = Files.newInputStream(Paths.get(classLoader.getResource(filePath).toURI()))) {
            byte[] bytes = inputStream.readAllBytes();
            result = new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.error("Could not open file: " + filePath);
            throw new RuntimeException(e);
        }
        return result;
    }
}
