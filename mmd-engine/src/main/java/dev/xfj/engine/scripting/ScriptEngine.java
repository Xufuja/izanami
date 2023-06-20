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

    public static void init() {
        data = new ScriptEngineData();
        initPolyglot();
        loadAssembly("scripts/MMD-ScriptCore.js");

        data.entityClass = new ScriptClass("Entity");

        Value instance = data.entityClass.instantiate();

        instance.invokeMember("printInt", 1);

        int value = 5;
        int value2 = 508;

        instance.invokeMember("printInts", value, value2);

        String string = "Hello World from Java!";

        instance.invokeMember("printCustomMessage", string);
    }

    public static void shutdown() {
        shutdownPolyglot();
        data = null;
    }

    private static void initPolyglot() {
        Context rootDomain = Context.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(className -> true).build();
        data.rootDomain = rootDomain;
    }

    private static void shutdownPolyglot() {
        data.coreAssembly = null;
        data.rootDomain.close();
        data.rootDomain = null;
    }

    public static void loadAssembly(String filePath) {
        data.coreAssembly = loadJavaScriptAssembly(filePath);
        data.rootDomain.eval("js", data.coreAssembly);
    }

}
