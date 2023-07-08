package dev.xfj.engine.scripting;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.core.UUID;
import dev.xfj.engine.scene.Entity;
import dev.xfj.engine.scene.Scene;
import dev.xfj.engine.scene.components.ScriptComponent;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScriptEngine {
    public static ScriptEngineData data = null;
    private static final ClassLoader classLoader = ScriptEngine.class.getClassLoader();

    private static String loadJavaScriptAssembly(String filePath, boolean core) {
        String result;
        Path path = null;
        if (core) {
            try {
                path = Path.of(classLoader.getResource(filePath).toURI());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            path = Paths.get(filePath);
        }
        try (InputStream inputStream = Files.newInputStream(path)) {
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
        loadAssembly("scripts/mmd-script-core.mjs");
        loadAppAssembly("sandbox-project/assets/scripts/dist/sandbox.mjs");
        loadAssemblyClasses();

        data.entityClass = new ScriptClass("Entity");
        /*data.entityClass = getEntityClasses().get("Player");

        Value instance = data.entityClass.instantiate();

        String printMessageFunc = data.entityClass.getMethod("printMessage", 0);
        data.entityClass.invokeMethod(instance, printMessageFunc);

        String printIntFunc = data.entityClass.getMethod("printInt", 1);

        int value = 5;

        data.entityClass.invokeMethod(instance, printIntFunc, value);

        String printIntsFunc = data.entityClass.getMethod("printInts", 2);

        int value2 = 508;

        data.entityClass.invokeMethod(instance, printIntsFunc, value, value2);

        String printCustomMessageFunc = data.entityClass.getMethod("printCustomMessage", 1);

        String string = "Hello World from Java!";

        data.entityClass.invokeMethod(instance, printCustomMessageFunc, string);*/
    }

    public static void shutdown() {
        shutdownPolyglot();
        data = null;
    }

    private static void initPolyglot() {
        try {
            Context rootDomain = Context.newBuilder()
                    .allowExperimentalOptions(true)
                    .allowIO(true)
                    .option("engine.WarnInterpreterOnly", "false")
                    .option("js.esm-eval-returns-exports", "true")
                    .allowHostAccess(HostAccess.ALL)
                    .allowHostClassLookup(className -> true).build();
            data.rootDomain = rootDomain;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void shutdownPolyglot() {
        data.coreAssembly = null;
        data.rootDomain.close();
        data.rootDomain = null;
    }

    public static void loadAssembly(String filePath) {
        try {
            data.coreAssembly = Source.newBuilder("js", loadJavaScriptAssembly(filePath, true), "mmd-script-core.mjs")
                    .mimeType("application/javascript+module")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void loadAppAssembly(String filePath) {
        try {
            data.appAssembly = Source.newBuilder("js", data.coreAssembly.getCharacters() + loadJavaScriptAssembly(filePath, false), "sandbox.mjs")
                    .mimeType("application/javascript+module")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void onRunTimeStart(Scene scene) {
        data.sceneContext = scene;
    }

    public static boolean entityClassExist(String className) {
        return data.entityClasses.containsKey(className);
    }

    public static void onCreateEntity(Entity entity) {
        ScriptComponent sc = entity.getComponent(ScriptComponent.class);

        if (entityClassExist(sc.className)) {
            ScriptInstance instance = new ScriptInstance(data.entityClasses.get(sc.className), entity);
            data.entityInstances.put(entity.getUUID(), instance);
            instance.invokeOnCreate();
        }
    }

    public static void onUpdateEntity(Entity entity, TimeStep ts) {
        UUID entityUUID = entity.getUUID();
        //Some sort of exception if not present in entityInstances //HZ_CORE_ASSERT(s_Data->EntityInstances.find(entityUUID) != s_Data->EntityInstances.end());
        ScriptInstance instance = data.entityInstances.get(entityUUID);
        if (instance != null) {
            instance.invokeOnUpdate(ts.getTime());
        }
    }

    public static Scene getSceneContext() {
        return data.sceneContext;
    }

    public static ScriptInstance getEntityScriptInstance(UUID uuid) {
        ScriptInstance scriptInstance = null;
        if (data.entityInstances.containsKey(uuid)) {
            scriptInstance = data.entityInstances.get(uuid);
        }
        return scriptInstance;
    }

    public static void onRuntimeStop() {
        data.sceneContext = null;
        data.entityInstances.clear();
    }

    public static Map<String, ScriptClass> getEntityClasses() {
        return data.entityClasses;
    }

    public static void loadAssemblyClasses() {
        //As far as I can see, there is no way to get all JS classes so just maintaining a Map inside the JS script
        Value exports = data.rootDomain.eval(data.appAssembly);
        Value classes = exports.getMember("classes");
        Map<String, List<String>> map = classes.as(Map.class);
        for (String clazz : map.get("entity")) {
            data.entityClasses.put(clazz, new ScriptClass(clazz));
        }
    }

    public static Value instantiateClass(String javaScriptClass, Object... params) {
        Value exports = ScriptEngine.data.rootDomain.eval(ScriptEngine.data.appAssembly);
        Value classConstructor = exports.getMember(javaScriptClass);
        //Object[] arguments = new Object[params.length + 1];
        //System.arraycopy(params, 0, arguments, 1, params.length);
        //arguments[0] = "classInstance";
        return classConstructor.newInstance(params);
    }
}
