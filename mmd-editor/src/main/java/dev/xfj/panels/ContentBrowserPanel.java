package dev.xfj.panels;

import dev.xfj.engine.renderer.Texture2D;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContentBrowserPanel {
    //Change later when this needs to be variable
    private static final Path assetPath = Path.of("assets");
    private Path currentDirectory;
    private final Texture2D directoryIcon;
    private final Texture2D fileIcon;
    private float padding;
    private float thumbnailSize;

    public ContentBrowserPanel() {
        currentDirectory = assetPath;
        ClassLoader classLoader = ContentBrowserPanel.class.getClassLoader();
        try {
            directoryIcon = Texture2D.create(Paths.get(classLoader.getResource("icons/contentbrowser/DirectoryIcon.png").toURI()));
            fileIcon = Texture2D.create(Paths.get(classLoader.getResource("icons/contentbrowser/FileIcon.png").toURI()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        this.padding = 16.0f;
        this.thumbnailSize = 128.0f;
    }

    public void onImGuiRender() {
        ImGui.begin("Content Browser");

        if (!currentDirectory.equals(assetPath)) {
            if (ImGui.button("<-")) {
                currentDirectory = currentDirectory.getParent();
            }
        }

        float cellSize = thumbnailSize + padding;

        float panelWidth = ImGui.getContentRegionAvail().x;
        int columnCount = (int) (panelWidth / cellSize);
        if (columnCount < 1) {
            columnCount = 1;
        }

        ImGui.columns(columnCount, "0", false);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(currentDirectory)) {
            for (Path directoryEntry : directoryStream) {
                Path relativePath = currentDirectory.relativize(directoryEntry);
                String filenameString = relativePath.getFileName().toString();

                Texture2D icon = Files.isDirectory(directoryEntry) ? directoryIcon : fileIcon;
                ImGui.imageButton(icon.getRendererId(), thumbnailSize, thumbnailSize, 0, 1, 1, 0);

                if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                    if (Files.isDirectory(directoryEntry)) {
                        currentDirectory = currentDirectory.resolve(directoryEntry.getFileName());
                    }
                }
                ImGui.textWrapped(filenameString);
                ImGui.nextColumn();

            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        ImGui.columns(1);

        float[] newThumbnailSize = new float[]{thumbnailSize};
        ImGui.sliderFloat("Thumbnail Size", newThumbnailSize, 16, 512);
        thumbnailSize = newThumbnailSize[0];

        float[] newPadding = new float[]{padding};
        ImGui.sliderFloat("Padding", newPadding, 0, 32);
        padding = newPadding[0];

        ImGui.end();
    }
}
