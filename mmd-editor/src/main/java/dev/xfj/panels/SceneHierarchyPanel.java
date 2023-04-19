package dev.xfj.panels;

import dev.xfj.engine.scene.Entity;
import dev.xfj.engine.scene.Scene;
import dev.xfj.engine.scene.components.TagComponent;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

public class SceneHierarchyPanel {
    private Scene context;
    private Entity selectionContext;

    public SceneHierarchyPanel() {
    }

    public SceneHierarchyPanel(Scene scene) {
        this.context = scene;
    }

    public void setContext(Scene scene) {
        context = scene;
    }

    public void onImGuiRender() {
        ImGui.begin("Scene Hierarchy");

        context.getRegistry().findEntitiesWith(TagComponent.class)
                .stream().forEach(result -> {
                    Entity entity = new Entity(result.entity(), context);
                    drawEntityNode(entity);
                });
        ImGui.end();
    }

    private void drawEntityNode(Entity entity) {
        String tag = entity.getComponent(TagComponent.class).tag;

        int flags = ((selectionContext == entity) ? ImGuiTreeNodeFlags.Selected : 0) | ImGuiTreeNodeFlags.OpenOnArrow;
        boolean opened = ImGui.treeNodeEx(entity.getId(), flags, tag);
        if (ImGui.isItemClicked()) {
            selectionContext = entity;
        }
        if (opened) {
            int flag = ImGuiTreeNodeFlags.OpenOnArrow;
            boolean open = ImGui.treeNodeEx(9817239, flag, tag);
            if (open) {
                ImGui.treePop();
            }
            ImGui.treePop();
        }
    }
}
