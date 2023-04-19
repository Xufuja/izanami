package dev.xfj.panels;

import dev.xfj.engine.scene.Entity;
import dev.xfj.engine.scene.Scene;
import dev.xfj.engine.scene.components.TagComponent;
import dev.xfj.engine.scene.components.TransformComponent;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import org.joml.Vector4f;

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
        if (ImGui.isMouseDown(0) && ImGui.isWindowHovered()) {
            selectionContext = null;
        }
        ImGui.end();
        ImGui.begin("Properties");
        if (selectionContext != null) {
            drawComponents(selectionContext);
        }
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

    private void drawComponents(Entity entity) {
        if (entity.hasComponent(TagComponent.class)) {
            TagComponent tagComponent = entity.getComponent(TagComponent.class);
            ImString buffer = new ImString(tagComponent.tag, 256);
            if (ImGui.inputText("Tag", buffer)) {
                tagComponent.tag = buffer.toString();
            }
        }

        if (entity.hasComponent(TransformComponent.class)) {
            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            if (ImGui.treeNodeEx(TransformComponent.class.hashCode(), ImGuiTreeNodeFlags.DefaultOpen, "Transform")) {
                Vector4f transform = transformComponent.transform.getColumn(3, new Vector4f());
                float[] newTransform = {transform.x, transform.y, transform.z};
                ImGui.dragFloat3("Position", newTransform, 0.1f);
                transformComponent.transform.setColumn(3, new Vector4f(newTransform[0], newTransform[1], newTransform[2], transform.w));

                ImGui.treePop();
            }
        }
    }
}
