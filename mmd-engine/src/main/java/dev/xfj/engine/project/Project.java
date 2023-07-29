package dev.xfj.engine.project;

import java.nio.file.Path;

public class Project {
    private ProjectConfig config;
    private Path projectDirectory;
    private static Project activProject;

    public static Path getProjectDirectory() {
        //Some sort of exception if activProject is null
        return activProject.projectDirectory;
    }

    public static Path getAssetDirectory() {
        //Some sort of exception if activProject is null
        return getProjectDirectory(); //Fix path with  / s_ActiveProject->m_Config.AssetDirectory;
    }

    public static Path getAssetFileSystemPath(Path path) {
        //Some sort of exception if activProject is null
        return getAssetDirectory(); //Fix path with / path;
    }

}
