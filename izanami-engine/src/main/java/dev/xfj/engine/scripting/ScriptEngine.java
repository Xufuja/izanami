package dev.xfj.engine.scripting;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.core.UUID;
import dev.xfj.engine.core.application.Application;
import dev.xfj.engine.project.Project;
import dev.xfj.engine.scene.Entity;
import dev.xfj.engine.scene.Scene;
import dev.xfj.engine.scene.components.ScriptComponent;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptEngine {
    public static ScriptEngineData data = null;
    private static final ClassLoader classLoader = ScriptEngine.class.getClassLoader();

    public enum ScriptFieldType {
        None,
        Float, Double,
        Bool, Char, Byte, Short, Int, Long,
        Vector2, Vector3, Vector4,
        Entity
    }

    public final static Map<String, ScriptFieldType> scriptFieldTypeMap = Map.ofEntries(
            Map.entry("f", ScriptFieldType.Float),
            Map.entry("d", ScriptFieldType.Double),
            Map.entry("b", ScriptFieldType.Bool),
            Map.entry("c", ScriptFieldType.Char),
            Map.entry("i18", ScriptFieldType.Byte),
            Map.entry("i16", ScriptFieldType.Short),
            Map.entry("i32", ScriptFieldType.Int),
            Map.entry("i64", ScriptFieldType.Long),
            Map.entry("v2", ScriptFieldType.Vector2),
            Map.entry("v3", ScriptFieldType.Vector3),
            Map.entry("v4", ScriptFieldType.Vector4),
            Map.entry("e", ScriptFieldType.Entity)
    );

    private static int getFirstUpperCaseIndex(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isUpperCase(value.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static ScriptFieldType toScriptFieldType(String name) {
        int variableNameStart = getFirstUpperCaseIndex(name);

        if (variableNameStart != -1) {
            String type = name.substring(0, variableNameStart);

            if (!scriptFieldTypeMap.containsKey(type)) {
                Log.error("Unknown type: " + type);
                return null;
            }

            return scriptFieldTypeMap.get(type);
        }
        throw new RuntimeException("No type found for variable: " + name);
    }

    private static String loadJavaScriptAssembly(Path filePath, boolean core) {
        String result;
        Path path = null;
        if (core) {
            try {
                path = Path.of(classLoader.getResource(filePath.toString()).toURI());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            path = filePath;
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

    private static FileAlterationObserver onAppAssemblyFileSystemEvent(Path filePath) {
        FileAlterationObserver observer = new FileAlterationObserver(filePath.getParent().toFile(), FileFilterUtils.suffixFileFilter(".mjs"));

        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                if (!data.assemblyReloadPending) {
                    data.assemblyReloadPending = true;
                    Application.getApplication().submitToMainThread(ScriptEngine::reloadAssembly);
                }
            }
        });
        return observer;
    }

    public static void init() {
        data = new ScriptEngineData();

        initPolyglot();

        boolean status = loadAssembly(Path.of("scripts/mmd-script-core.mjs"));

        if (!status) {
            Log.error("[ScriptEngine] Could not load ScriptCore assembly.");
            return;
        }

        Path scriptModulePath = Project.getAssetDirectory().resolve(Project.getActive().getConfig().scriptModulePath);

        status = loadAppAssembly(scriptModulePath);

        if (!status) {
            Log.error("[ScriptEngine] Could not load app assembly.");
            return;
        }

        loadAssemblyClasses();

        data.entityClass = new ScriptClass("Entity");
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

    public static boolean loadAssembly(Path filePath) {
        try {
            data.coreAssemblyFilepath = filePath;
            data.coreAssembly = Source.newBuilder("js", loadJavaScriptAssembly(filePath, true), "mmd-script-core.mjs")
                    .mimeType("application/javascript+module")
                    .build();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean loadAppAssembly(Path filePath) {
        try {
            data.appAssemblyFilepath = filePath;
            data.appAssembly = Source.newBuilder("js", data.coreAssembly.getCharacters() + loadJavaScriptAssembly(filePath, false), "sandbox.mjs")
                    .mimeType("application/javascript+module")
                    .build();

            data.appAssemblyFileWatcher = new FileAlterationMonitor(5000);
            FileAlterationObserver observer = onAppAssemblyFileSystemEvent(filePath);

            data.appAssemblyFileWatcher.addObserver(observer);
            data.appAssemblyFileWatcher.start();
            data.assemblyReloadPending = false;
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static void reloadAssembly() {
        initPolyglot();
        loadAssembly(data.coreAssemblyFilepath);
        loadAppAssembly(data.appAssemblyFilepath);
        loadAssemblyClasses();

        data.entityClass = new ScriptClass("Entity");
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
            UUID entityId = entity.getUUID();
            ScriptInstance instance = new ScriptInstance(data.entityClasses.get(sc.className), entity);
            data.entityInstances.put(entityId, instance);

            if (data.entityScriptFields.containsKey(entityId)) {
                Map<String, ScriptFieldInstance> fieldMap = data.entityScriptFields.get(entityId);

                for (Map.Entry<String, ScriptFieldInstance> entry : fieldMap.entrySet()) {
                    instance.setFieldValueInternal(entry.getKey(), entry.getValue().buffer);
                }
            }

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

    public static ScriptClass getEntityClass(String name) {
        if (!data.entityClasses.containsKey(name)) {
            return null;
        }
        return data.entityClasses.get(name);
    }

    public static void onRuntimeStop() {
        data.sceneContext = null;
        data.entityInstances.clear();
    }

    public static Map<String, ScriptClass> getEntityClasses() {
        return data.entityClasses;
    }

    public static Map<String, ScriptFieldInstance> getScriptFieldMap(Entity entity) {
        //Some sort of exception
        if (entity == null) {
            throw new RuntimeException();
        }

        UUID entityId = entity.getUUID();
        return data.entityScriptFields.get(entityId);
    }

    public static void addToScriptFieldMap(Entity entity) {
        UUID entityId = entity.getUUID();
        data.entityScriptFields.put(entityId, new HashMap<>());
    }

    public static void removeFromScriptFieldMap(Entity entity) {
        UUID entityId = entity.getUUID();
        data.entityScriptFields.remove(entityId);
    }

    public static void loadAssemblyClasses() {
        Value exports = data.rootDomain.eval(data.appAssembly);

        for (var className : exports.getMemberKeys()) {
            Value current = exports.getMember(className);
            boolean isEntity = isSubClassOf(current, className, "Entity");

            if (!isEntity) {
                continue;
            }

            ScriptClass scriptClass = new ScriptClass(className);
            data.entityClasses.put(className, scriptClass);

            //Needed to get the public fields for both the super class and the subclass
            List<String> fields = getPublicClassFields(current, getPublicClassFields(exports.getMember("Entity"), new ArrayList<>()));

            Log.warn(String.format("%1$s has %2$s fields:", className, fields.size()));

            fields.forEach(field -> {
                String name = field.substring(getFirstUpperCaseIndex(field));
                ScriptFieldType fieldType = toScriptFieldType(field);
                Log.warn(String.format("  %1$s (%2$s)", field, scriptFieldTypeToString(fieldType)));
                scriptClass.fields.put(field, new ScriptField(fieldType, name));
            });
        }
    }

    //I guess it works
    private static boolean isSubClassOf(Value clazz, String name, String parent) {
        String input = String.valueOf(clazz);

        if (input.startsWith("class")) {
            String signature = input.substring(input.indexOf(" ") + 1, input.indexOf("{") - 1);

            if (!signature.contains("extends")) {
                return false;
            } else {
                return signature.equals(String.format("%1$s extends %2$s", name, parent));
            }

        } else {
            return false;
        }
    }

    //Even worse than the isSubClassOf() method, surely there must be a better way than this
    //But getMemberKeys() does not get fields for some reason
    private static List<String> getPublicClassFields(Value clazz, List<String> result) {
        String input = String.valueOf(clazz);

        if (input.startsWith("class")) {
            String fieldBlock = input.substring(input.indexOf("{") + 1, input.indexOf("constructor") - 1);

            String[] fields = fieldBlock.split("\r\n");

            for (String field : fields) {
                field = field.trim();

                //# indicates a private field
                if (field.isBlank() || field.isEmpty() || field.startsWith("#")) {
                    continue;
                }

                String[] temp = field.replace(";", "").split(" ");

                if (temp.length > 1) {
                    result.add(temp[0]);
                } else {
                    result.add(field);
                }
            }
        }
        return result;
    }

    public static Value getManagedInstance(UUID uuid) {
        return data.entityInstances.get(uuid).getManagedObject();
    }

    public static Value instantiateClass(String javaScriptClass, Object... params) {
        Value exports = ScriptEngine.data.rootDomain.eval(ScriptEngine.data.appAssembly);
        Value classConstructor = exports.getMember(javaScriptClass);

        return classConstructor.newInstance(params);
    }

    public static String scriptFieldTypeToString(ScriptFieldType type) {
        return switch (type) {
            case Float -> "Float";
            case Double -> "Double";
            case Bool -> "Bool";
            case Char -> "Char";
            case Byte -> "Byte";
            case Short -> "Short";
            case Int -> "Int";
            case Long -> "Long";
            case Vector2 -> "Vector2";
            case Vector3 -> "Vector3";
            case Vector4 -> "Vector4";
            case Entity -> "Entity";
            default -> "None";
        };
    }

    public static ScriptFieldType scriptFieldTypeFromString(String fieldType) {
        return switch (fieldType) {
            case "Float" -> ScriptFieldType.Float;
            case "Double" -> ScriptFieldType.Double;
            case "Bool" -> ScriptFieldType.Bool;
            case "Char" -> ScriptFieldType.Char;
            case "Byte" -> ScriptFieldType.Byte;
            case "Short" -> ScriptFieldType.Short;
            case "Int" -> ScriptFieldType.Int;
            case "Long" -> ScriptFieldType.Long;
            case "Vector2" -> ScriptFieldType.Vector2;
            case "Vector3" -> ScriptFieldType.Vector3;
            case "Vector4" -> ScriptFieldType.Vector4;
            case "Entity" -> ScriptFieldType.Entity;
            default -> ScriptFieldType.None;
        };
    }
}
