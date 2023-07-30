package dev.xfj.engine.project;

import com.google.protobuf.util.JsonFormat;
import dev.xfj.engine.core.Log;
import dev.xfj.protobuf.ProjectFile;
import dev.xfj.protobuf.ProjectNodeFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ProjectSerializer {
    private static final boolean PROJECTS_AS_JSON = true;
    private final Project project;

    public ProjectSerializer(Project project) {
        this.project = project;
    }

    public void serialize(Path filePath) {
        ProjectConfig config = project.getConfig();

        ProjectFile.Builder projectBuilder = ProjectFile.newBuilder();
        ProjectNodeFile.Builder projectNodeBuilder = ProjectNodeFile.newBuilder();
        projectNodeBuilder.setName(config.name);
        projectNodeBuilder.setStartScene(config.startScene.toString());
        projectNodeBuilder.setAssetDirectory(config.assetDirectory.toString());
        projectNodeBuilder.setScriptModulePath(config.scriptModulePath.toString());
        projectBuilder.setProjectNode(projectNodeBuilder);


        if (PROJECTS_AS_JSON) {
            try {

                Files.writeString(filePath, JsonFormat.printer().print(projectBuilder), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                Log.error("Could not open file: " + filePath);
                throw new RuntimeException(e);
            }
        } else {
            byte[] bytes = projectBuilder.build().toByteArray();
            try (OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                outputStream.write(bytes);
            } catch (IOException e) {
                Log.error("Could not open file: " + filePath);
                throw new RuntimeException(e);
            }
        }
    }
    public boolean deserialize(Path filePath) {
        ProjectConfig config = project.getConfig();

        ProjectFile.Builder projectBuilder = ProjectFile.newBuilder();

        if (PROJECTS_AS_JSON) {
            try {
                JsonFormat.parser().merge(Files.readString(filePath), projectBuilder);
            } catch (IOException e) {
                Log.error(String.format("Failed to load project file '%1$s'\n     {%2$s}", filePath, e.getMessage()));
                throw new RuntimeException(e);
            }
        } else {
            try (InputStream inputStream = Files.newInputStream(filePath)) {
                byte[] bytes = inputStream.readAllBytes();
                projectBuilder.mergeFrom(bytes);
            } catch (IOException e) {
                Log.error(String.format("Failed to load project file '%1$s'\n     {%2$s}", filePath, e.getMessage()));
                throw new RuntimeException(e);
            }
        }

        ProjectFile projectFile = projectBuilder.build();

        if (!projectFile.hasProjectNode()) {
            return false;
        }

        ProjectNodeFile projectNode = projectFile.getProjectNode();
        config.name = projectNode.getName();
        config.startScene = Path.of(projectNode.getStartScene());
        config.assetDirectory = Path.of(projectNode.getAssetDirectory());
        config.scriptModulePath = Path.of(projectNode.getScriptModulePath());
        return true;
    }
}
