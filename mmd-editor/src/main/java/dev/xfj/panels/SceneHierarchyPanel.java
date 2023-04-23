package dev.xfj.panels;

import dev.xfj.engine.scene.Entity;
import dev.xfj.engine.scene.Scene;
import dev.xfj.engine.scene.SceneCamera;
import dev.xfj.engine.scene.components.CameraComponent;
import dev.xfj.engine.scene.components.SpriteRendererComponent;
import dev.xfj.engine.scene.components.TagComponent;
import dev.xfj.engine.scene.components.TransformComponent;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import org.joml.Vector3f;
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
        if (ImGui.beginPopupContextWindow("0", ImGuiPopupFlags.MouseButtonRight | ImGuiPopupFlags.NoOpenOverItems)) {
            if (ImGui.menuItem("Create Empty Entity")) {
                context.createEntity("Empty Entity");
            }
            ImGui.endPopup();
        }
        ImGui.end();
        ImGui.begin("Properties");
        if (selectionContext != null) {
            drawComponents(selectionContext);

            if (ImGui.button("Add Component")) {
                ImGui.openPopup("AddComponent");
            }

            if (ImGui.beginPopup("AddComponent")) {
                if (ImGui.menuItem("Camera")) {
                    selectionContext.addComponent(new CameraComponent());
                    ImGui.closeCurrentPopup();
                }
                if (ImGui.menuItem("Sprite Renderer")) {
                    selectionContext.addComponent(new SpriteRendererComponent());
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }
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

        boolean entityDeleted = false;
        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem("Delete Entity")) {
                entityDeleted = true;
            }
            ImGui.endPopup();
        }

        if (opened) {
            int flag = ImGuiTreeNodeFlags.OpenOnArrow;
            boolean open = ImGui.treeNodeEx(9817239, flag, tag);
            if (open) {
                ImGui.treePop();
            }
            ImGui.treePop();
        }
        if (entityDeleted) {
            context.destroyEntity(entity.getEntity());
            //I suppose in the C++ version the selectionContext is empty rather than null so there this is not needed
            if (selectionContext != null && selectionContext.equals(entity)) {
                selectionContext = null;
            }
        }
    }

    private static void drawVec3Control(String label, Vector3f values) {
        drawVec3Control(label, values, 0.0f, 100.0f);
    }

    private static void drawVec3Control(String label, Vector3f values, float resetValue) {
        drawVec3Control(label, values, resetValue, 100.0f);
    }

    private static void drawVec3Control(String label, Vector3f values, float resetValue, float columWidth) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, columWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        //There do not appear to be bindings for ImGui::PushMultiItemsWidths() so doing it the manual way
        ImGui.pushItemWidth(ImGui.calcItemWidth() / 3);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        float lineHeight = ImGui.getFont().getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        ImVec2 buttonSize = new ImVec2(lineHeight + 3.0f, lineHeight);

        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);

        if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
            values.x = resetValue;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] newX = new float[]{values.x};
        ImGui.dragFloat("##X", newX, 0.1f, 0.0f, 0.0f, "%.2f");
        values.x = newX[0];
        ImGui.sameLine();

        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);

        if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            values.y = resetValue;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] newY = new float[]{values.y};
        ImGui.dragFloat("##Y", newY, 0.1f, 0.0f, 0.0f, "%.2f");
        values.y = newY[0];
        ImGui.sameLine();

        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.25f, 0.8f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.35f, 0.9f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.25f, 0.8f, 1.0f);

        if (ImGui.button("Z", buttonSize.x, buttonSize.y)) {
            values.z = resetValue;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] newZ = new float[]{values.z};
        ImGui.dragFloat("##Z", newZ, 0.1f, 0.0f, 0.0f, "%.2f");
        values.z = newZ[0];
        ImGui.popItemWidth();

        ImGui.popStyleVar();
        ImGui.columns(1);

        ImGui.popID();
    }

    private void drawComponents(Entity entity) {
        if (entity.hasComponent(TagComponent.class)) {
            TagComponent tagComponent = entity.getComponent(TagComponent.class);
            ImString buffer = new ImString(tagComponent.tag, 256);
            if (ImGui.inputText("Tag", buffer)) {
                tagComponent.tag = buffer.toString();
            }
        }

        int treeNodeFlags = ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.AllowItemOverlap;

        if (entity.hasComponent(TransformComponent.class)) {
            boolean open = ImGui.treeNodeEx(TransformComponent.class.hashCode(), treeNodeFlags, "Transform");
            if (open) {
                TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

                drawVec3Control("Translation", transformComponent.translation);

                Vector3f rotation = transformComponent.rotation.mul((float) Math.toDegrees(1.0), new Vector3f());
                drawVec3Control("Rotation", rotation);
                transformComponent.rotation = rotation.mul((float) Math.toRadians(1.0), new Vector3f());

                drawVec3Control("Scale", transformComponent.scale, 1.0f);

                ImGui.treePop();
            }
        }
        if (entity.hasComponent(CameraComponent.class)) {
            if (ImGui.treeNodeEx(CameraComponent.class.hashCode(), treeNodeFlags, "Camera")) {
                CameraComponent cameraComponent = entity.getComponent(CameraComponent.class);
                SceneCamera camera = cameraComponent.camera;

                if (ImGui.checkbox("Primary", cameraComponent.primary)) {
                    cameraComponent.primary = !cameraComponent.primary;
                }

                String[] projectionTypeStrings = new String[]{"Perspective", "Orthographic"};
                String currentProjectionTypeString = projectionTypeStrings[camera.getProjectionType().ordinal()];

                if (ImGui.beginCombo("Projection", currentProjectionTypeString)) {
                    for (int i = 0; i < 2; i++) {
                        boolean isSelected = currentProjectionTypeString.equals(projectionTypeStrings[i]);
                        if (ImGui.selectable(projectionTypeStrings[i], isSelected)) {
                            currentProjectionTypeString = projectionTypeStrings[i];
                            camera.setProjectionType(SceneCamera.ProjectionType.values()[i]);
                        }
                        if (isSelected) {
                            ImGui.setItemDefaultFocus();
                        }
                    }
                    ImGui.endCombo();
                }

                if (camera.getProjectionType() == SceneCamera.ProjectionType.Perspective) {
                    float[] perspectiveVerticalFoV = new float[]{(float) Math.toDegrees(camera.getPerspectiveVerticalFoV())};
                    if (ImGui.dragFloat("Vertical FoV", perspectiveVerticalFoV)) {
                        camera.setPerspectiveVerticalFoV((float) Math.toRadians(perspectiveVerticalFoV[0]));
                    }

                    float[] perspectiveNear = new float[]{camera.getPerspectiveNearClip()};
                    if (ImGui.dragFloat("Near", perspectiveNear)) {
                        camera.setPerspectiveNearClip(perspectiveNear[0]);
                    }

                    float[] perspectiveFar = new float[]{camera.getPerspectiveFarClip()};
                    if (ImGui.dragFloat("Far", perspectiveFar)) {
                        camera.setPerspectiveFarClip(perspectiveFar[0]);
                    }
                }

                if (camera.getProjectionType() == SceneCamera.ProjectionType.Orthographic) {
                    float[] orthoSize = new float[]{camera.getOrthographicSize()};
                    if (ImGui.dragFloat("Size", orthoSize)) {
                        camera.setOrthographicSize(orthoSize[0]);
                    }

                    float[] orthoNear = new float[]{camera.getOrthographicNearClip()};
                    if (ImGui.dragFloat("Near", orthoNear)) {
                        camera.setOrthographicNearClip(orthoNear[0]);
                    }

                    float[] orthoFar = new float[]{camera.getOrthographicFarClip()};
                    if (ImGui.dragFloat("Far", orthoFar)) {
                        camera.setOrthographicFarClip(orthoFar[0]);
                    }

                    if (ImGui.checkbox("Fixed Aspect Ratio", cameraComponent.fixedAspectRatio)) {
                        cameraComponent.fixedAspectRatio = !cameraComponent.fixedAspectRatio;
                    }
                }
                ImGui.treePop();
            }
        }
        if (entity.hasComponent(SpriteRendererComponent.class)) {
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4, 4);
            boolean open = ImGui.treeNodeEx(SpriteRendererComponent.class.hashCode(), treeNodeFlags, "Sprite Renderer");
            ImGui.sameLine(ImGui.getWindowWidth() - 25.0f);

            if (ImGui.button("+", 20, 20)) {
                ImGui.openPopup("ComponentSettings");
            }

            ImGui.popStyleVar();
            boolean removeComponent = false;

            if (ImGui.beginPopup("ComponentSettings")) {
                if (ImGui.menuItem("Remove component")) {
                    removeComponent = true;
                }
                ImGui.endPopup();
            }

            if (open) {
                //There is no equivalent to glm::value_ptr(m_SquareColor) so doing it this way
                SpriteRendererComponent src = entity.getComponent(SpriteRendererComponent.class);
                float[] newColor = {src.color.x, src.color.y, src.color.z, src.color.w};
                ImGui.colorEdit4("Color", newColor);
                src.color = new Vector4f(newColor[0], newColor[1], newColor[2], newColor[3]);
                ImGui.treePop();
            }
            if (removeComponent) {
                entity.removeComponent(SpriteRendererComponent.class);
            }
        }
    }
}
