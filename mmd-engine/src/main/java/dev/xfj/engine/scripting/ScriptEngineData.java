package dev.xfj.engine.scripting;

import dev.xfj.engine.core.UUID;
import dev.xfj.engine.scene.Scene;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

public class ScriptEngineData {
    public Context rootDomain = null;
    public Source coreAssembly = null;
    public Source appAssembly = null;
    public Path coreAssemblyFilepath = null;
    public Path appAssemblyFilepath = null;
    public ScriptClass entityClass = null;
    public Map<String, ScriptClass> entityClasses = new HashMap<>();
    public Map<UUID, ScriptInstance> entityInstances =  new HashMap<>();
    public Map<UUID, Map<String, ScriptFieldInstance>> entityScriptFields = new HashMap<>();
    public FileAlterationMonitor appAssemblyFileWatcher = null;
    public boolean assemblyReloadPending = false;
    public Scene sceneContext = null;

}
