package dev.xfj.engine.scripting;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import dev.xfj.engine.core.Log;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;

import javax.script.Invocable;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScriptEngine {
    public static ScriptEngineData data = new ScriptEngineData();

    public static void init() {
        initNashorn();
    }

    public static void shutdown() {
        shutdownNashorn();
    }

    private static void initNashorn() {
        data.scriptEngine = GraalJSScriptEngine.create(
                Engine.newBuilder()
                        .option("engine.WarnInterpreterOnly", "false")
                        .build(),
                Context.newBuilder("js"));
        data.coreJavaScript = loadJavaScriptFile("scripts/MMD-ScriptCore.js");

        try {
            data.scriptEngine.eval(data.coreJavaScript);
            Invocable invocable = (Invocable) data.scriptEngine;

            invocable.invokeFunction("printMessage");

            invocable.invokeFunction("printInt", 1);

            int value = 5;
            invocable.invokeFunction("printInt", value);

            int value2 = 508;
            invocable.invokeFunction("printInts", value, value2);

            String string = "Hello World from Java!";

            invocable.invokeFunction("printCustomMessage", string);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void shutdownNashorn() {
        data.scriptEngine = null;
        data.coreJavaScript = null;
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
