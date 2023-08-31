package dev.xfj.engine.project;

import java.nio.file.Path;

public class Project {
    private final ProjectConfig config;
    private Path projectDirectory;
    private static Project activeProject;

    public Project() {
        this.config = new ProjectConfig();
    }

    public static Project newProject() {
        activeProject = new Project();
        return activeProject;
    }

    public static Project loadProject(Path path) {
        Project project = new Project();
        ProjectSerializer serializer = new ProjectSerializer(project);

        if (serializer.deserialize(path)) {
            project.projectDirectory = path.getParent();
            activeProject = project;
            return activeProject;
        }

        return null;
    }

    public static boolean saveActive(Path path) {
        ProjectSerializer serializer = new ProjectSerializer(activeProject);

        if (serializer.serialize(path)) {
            activeProject.projectDirectory = path.getParent();
            return true;
        }

        return false;
    }

    public static Path getProjectDirectory() {
        //Some sort of exception if activeProject is null
        return activeProject.projectDirectory;
    }

    public static Path getAssetDirectory() {
        //Some sort of exception if activeProject is null
        return getProjectDirectory().resolve(activeProject.config.assetDirectory);
    }

    public static Path getAssetFileSystemPath(Path path) {
        //Some sort of exception if activeProject is null
        return getAssetDirectory().resolve(path);
    }

    public ProjectConfig getConfig() {
        return config;
    }

    public static Project getActive() {
        return activeProject;
    }
}
