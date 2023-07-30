package dev.xfj.engine.project;

import java.nio.file.Path;

public class Project {
    private ProjectConfig config;
    private Path projectDirectory;
    private static Project activProject;

    public static Path getProjectDirectory() {
        //Some sort of exception if activeProject is null
        return activProject.projectDirectory;
    }

    public static Path getAssetDirectory() {
        //Some sort of exception if activProject is null
        return getProjectDirectory().resolve(activProject.config.assetDirectory);
    }

    public static Path getAssetFileSystemPath(Path path) {
        //Some sort of exception if activProject is null
        return getAssetDirectory().resolve(path);
    }

    public ProjectConfig getConfig() {
        return config;
    }

    public static Project getActive() {
        return activProject;
    }

    public static Project newProject() {
        activProject = new Project();
        return activProject;
    }

    public static Project loadProject() {
        Project project = new Project();
        ProjectSerializer serializer = new ProjectSerializer(project);
        return activProject;
    }

    public static boolean saveActive() {
        return false;
    }
}
