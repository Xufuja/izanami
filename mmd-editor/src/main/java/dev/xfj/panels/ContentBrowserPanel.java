package dev.xfj.panels;

import imgui.ImGui;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ContentBrowserPanel {
    //Change later when this needs to be variable
    private static final Path assetPath = Path.of("assets");
    private Path currentDirectory;

    public ContentBrowserPanel() {
        currentDirectory = assetPath;
    }

    public void onImGuiRender() {
        ImGui.begin("Content Browser");

        if (!currentDirectory.equals(assetPath)) {
            if (ImGui.button("<-")) {
                currentDirectory = currentDirectory.getParent();
            }
        }

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(currentDirectory)) {
            for (Path directoryEntry : directoryStream) {
                Path relativePath = currentDirectory.relativize(directoryEntry);
                String filenameString = relativePath.getFileName().toString();

                if (Files.isDirectory(directoryEntry)) {
                    if (ImGui.button(filenameString)) {
                        currentDirectory = currentDirectory.resolve(directoryEntry.getFileName());
                    }
                } else {
                    if (ImGui.button(filenameString)) {
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        ImGui.end();
    }
}
