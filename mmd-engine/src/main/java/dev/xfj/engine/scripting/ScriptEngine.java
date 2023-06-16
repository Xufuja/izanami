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
    public static ScriptEngineData data = null;

    public static void init() {
        data = new ScriptEngineData();
        initPolyglot();
        loadAssembly("scripts/MMD-ScriptCore.js");

        Value classConstructor = data.rootDomain.eval("js", "Main");
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
        instance.invokeMember("getApplicationName");
    }

    public static void shutdown() {
        shutdownPolyglot();
        data.rootDomain.close();
    }

    private static void initPolyglot() {
        Context rootDomain = Context.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(clazz -> clazz.equals("dev.xfj.engine.core.Log") || clazz.equals("dev.xfj.engine.core.application.Application")).build();
        data.rootDomain = rootDomain;
    }

    private static void shutdownPolyglot() {
        data.coreAssembly = null;
    }

    public static void loadAssembly(String filePath) {
        data.coreAssembly = loadJavaScriptAssembly(filePath);
        data.rootDomain.eval("js", data.coreAssembly);
    }

    private static String loadJavaScriptAssembly(String filePath) {
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
