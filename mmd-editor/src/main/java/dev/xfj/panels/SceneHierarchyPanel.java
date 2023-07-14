package dev.xfj.panels;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.imgui.ImGuiLayer;
import dev.xfj.engine.imgui.ImGuiPayload;
import dev.xfj.engine.renderer.Texture2D;
import dev.xfj.engine.scene.Entity;
import dev.xfj.engine.scene.Scene;
import dev.xfj.engine.scene.SceneCamera;
import dev.xfj.engine.scene.components.*;
import dev.xfj.engine.scripting.*;
import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

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
        selectionContext = null;
    }

    public Entity getSelectedEntity() {
        return selectionContext;
    }

    public void onImGuiRender() {
        ImGui.begin("Scene Hierarchy");

        if (context != null) {
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
        }
        ImGui.end();
        ImGui.begin("Properties");
        if (selectionContext != null) {
            drawComponents(selectionContext);
        }
        ImGui.end();
    }


    public void setSelectedEntity(Entity entity) {
        selectionContext = entity;
    }

    private void drawEntityNode(Entity entity) {
        String tag = entity.getComponent(TagComponent.class).tag;

        int flags = (selectionContext != null && selectionContext.equals(entity) ? ImGuiTreeNodeFlags.Selected : 0) | ImGuiTreeNodeFlags.OpenOnArrow;
        flags |= ImGuiTreeNodeFlags.SpanAvailWidth;

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
            int flag = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
            boolean open = ImGui.treeNodeEx(9817239, flag, tag);
            if (open) {
                ImGui.treePop();
            }
            ImGui.treePop();
        }
        if (entityDeleted) {
            context.destroyEntity(entity);
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
        ImFont boldFont = ImGuiLayer.fonts[0];
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

        ImGui.pushFont(boldFont);
        if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
            values.x = resetValue;
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] newX = new float[]{values.x};
        ImGui.dragFloat("##X", newX, 0.1f, 0.0f, 0.0f, "%.2f");
        values.x = newX[0];
        ImGui.sameLine();

        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);

        ImGui.pushFont(boldFont);
        if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            values.y = resetValue;
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] newY = new float[]{values.y};
        ImGui.dragFloat("##Y", newY, 0.1f, 0.0f, 0.0f, "%.2f");
        values.y = newY[0];
        ImGui.sameLine();

        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.25f, 0.8f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.35f, 0.9f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.25f, 0.8f, 1.0f);

        ImGui.pushFont(boldFont);
        if (ImGui.button("Z", buttonSize.x, buttonSize.y)) {
            values.z = resetValue;
        }
        ImGui.popFont();
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

    private static <T> void drawComponent(Class<T> componentType, String name, Entity entity, Consumer<T> uiHandler) {
        int treeNodeFlags = ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.AllowItemOverlap | ImGuiTreeNodeFlags.FramePadding;

        if (entity.hasComponent(componentType)) {
            var component = entity.getComponent(componentType);

            ImVec2 contentRegionAvailable = ImGui.getContentRegionAvail();

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4, 4);
            float lineHeight = ImGui.getFont().getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
            ImGui.separator();
            boolean open = ImGui.treeNodeEx(componentType.hashCode(), treeNodeFlags, name);
            ImGui.popStyleVar();

            ImGui.sameLine(contentRegionAvailable.x - lineHeight * 0.5f);
            if (ImGui.button("+", lineHeight, lineHeight)) {
                ImGui.openPopup("ComponentSettings");
            }

            boolean removeComponent = false;

            if (ImGui.beginPopup("ComponentSettings")) {
                if (ImGui.menuItem("Remove component")) {
                    removeComponent = true;
                }
                ImGui.endPopup();
            }

            if (open) {
                uiHandler.accept(component);
                ImGui.treePop();
            }

            if (removeComponent) {
                entity.removeComponent(componentType);
            }

        }
    }

    private void drawComponents(Entity entity) {
        if (entity.hasComponent(TagComponent.class)) {
            TagComponent tagComponent = entity.getComponent(TagComponent.class);
            ImString buffer = new ImString(tagComponent.tag, 256);
            if (ImGui.inputText("##Tag", buffer)) {
                tagComponent.tag = buffer.toString();
            }
        }

        ImGui.sameLine();
        ImGui.pushItemWidth(-1);

        if (ImGui.button("Add Component")) {
            ImGui.openPopup("AddComponent");
        }

        if (ImGui.beginPopup("AddComponent")) {
            displayAddComponentEntry(CameraComponent.class, "Camera");
            displayAddComponentEntry(ScriptComponent.class, "Script");
            displayAddComponentEntry(SpriteRendererComponent.class, "Sprite Renderer");
            displayAddComponentEntry(CircleRendererComponent.class, "Circle Renderer");
            displayAddComponentEntry(Rigidbody2DComponent.class, "Rigidbody 2D");
            displayAddComponentEntry(BoxCollider2DComponent.class, "Box Collider 2D");
            displayAddComponentEntry(CircleCollider2DComponent.class, "Circle Collider 2D");

            ImGui.endPopup();
        }

        ImGui.popItemWidth();

        drawComponent(TransformComponent.class, "Transform", entity, component -> {
            drawVec3Control("Translation", component.translation);

            Vector3f rotation = component.rotation.mul((float) Math.toDegrees(1.0), new Vector3f());
            drawVec3Control("Rotation", rotation);
            component.rotation = rotation.mul((float) Math.toRadians(1.0), new Vector3f());

            drawVec3Control("Scale", component.scale, 1.0f);
        });

        drawComponent(CameraComponent.class, "Camera", entity, component -> {
            SceneCamera camera = component.camera;

            if (ImGui.checkbox("Primary", component.primary)) {
                component.primary = !component.primary;
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

                if (ImGui.checkbox("Fixed Aspect Ratio", component.fixedAspectRatio)) {
                    component.fixedAspectRatio = !component.fixedAspectRatio;
                }
            }
        });

        drawComponent(ScriptComponent.class, "Script", entity, component -> {
            boolean scriptClassExists = ScriptEngine.entityClassExist(component.className);

            ImString buffer = new ImString(component.className, 64);

            if (!scriptClassExists) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0.9f, 0.2f, 0.3f, 1.0f);
            }

            if (ImGui.inputText("Class", buffer)) {
                component.className = buffer.toString();
            }

            boolean sceneRunning = context.isRunning();

            if (sceneRunning) {
                ScriptInstance scriptInstance = ScriptEngine.getEntityScriptInstance(entity.getUUID());

                if (scriptInstance != null) {
                    Map<String, ScriptField> fields = scriptInstance.getScriptClass().getFields();

                    for (Map.Entry<String, ScriptField> entry : fields.entrySet()) {
                        if (entry.getValue().type == ScriptEngine.ScriptFieldType.Float) {
                            float[] newData = new float[]{scriptInstance.getFieldValue(Float.class, entry.getKey())};

                            if (ImGui.dragFloat(entry.getValue().name, newData)) {
                                scriptInstance.setFieldValue(entry.getKey(), newData[0]);
                            }
                        }
                    }
                }
            } else {
                if (scriptClassExists) {
                    ScriptClass entityClass = ScriptEngine.getEntityClass(component.className);
                    Map<String, ScriptField> fields = entityClass.getFields();

                    //Somehow, the C++ version has entries in the map without ever adding anything
                    if (ScriptEngine.getScriptFieldMap(entity) == null) {
                        ScriptEngine.addToScriptFieldMap(entity);
                    }

                    Map<String, ScriptFieldInstance> entityFields = ScriptEngine.getScriptFieldMap(entity);

                    for (Map.Entry<String, ScriptField> entry : fields.entrySet()) {
                        if (entityFields.containsKey(entry.getKey())) {
                            ScriptFieldInstance scriptField = entityFields.get(entry.getKey());

                            if (entry.getValue().type == ScriptEngine.ScriptFieldType.Float) {
                                float[] newData = new float[]{scriptField.getValue(Float.class)};

                                if (ImGui.dragFloat(entry.getValue().name, newData)) {
                                    scriptField.setValue(newData[0]);
                                }
                            }
                        } else {
                            if (entry.getValue().type == ScriptEngine.ScriptFieldType.Float) {
                                float[] newData = new float[]{0.0f};

                                if (ImGui.dragFloat(entry.getValue().name, newData)) {
                                    //Just like earlier, this somehow just "exists" in the C++ version
                                    if (entityFields.get(entry.getKey()) == null) {
                                        ScriptFieldInstance fieldInstance = new ScriptFieldInstance();
                                        fieldInstance.field = entry.getValue();
                                        fieldInstance.setValue(newData[0]);
                                        entityFields.put(entry.getKey(), fieldInstance);
                                    } else {
                                        ScriptFieldInstance fieldInstance = entityFields.get(entry.getKey());
                                        fieldInstance.field = entry.getValue();
                                        fieldInstance.setValue(newData[0]);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!scriptClassExists) {
                ImGui.popStyleColor();
            }
        });

        drawComponent(SpriteRendererComponent.class, "Sprite Renderer", entity, component -> {
            SpriteRendererComponent src = entity.getComponent(SpriteRendererComponent.class);

            float[] newColor = {src.color.x, src.color.y, src.color.z, src.color.w};
            ImGui.colorEdit4("Color", newColor);
            src.color = new Vector4f(newColor[0], newColor[1], newColor[2], newColor[3]);

            ImGui.button("Texture", 100.0f, 0.0f);
            if (ImGui.beginDragDropTarget()) {
                ImGuiPayload<?> payload = ImGui.acceptDragDropPayload("CONTENT_BROWSER_ITEM");
                if (payload != null) {
                    Path texturePath = ContentBrowserPanel.assetPath.resolve(String.valueOf(payload.getData()));
                    Texture2D texture = Texture2D.create(texturePath);

                    if (texture.isLoaded()) {
                        component.texture = texture;
                    } else {
                        Log.warn(String.format("Could not load texture %s", texturePath));
                    }
                }
                ImGui.endDragDropTarget();
            }
            float[] newTilingFactor = new float[]{component.tilingFactor};
            ImGui.dragFloat("Tiling Factor", newTilingFactor, 0.1f, 0.0f, 100.0f);
            component.tilingFactor = newTilingFactor[0];
        });

        drawComponent(CircleRendererComponent.class, "Circle Renderer", entity, component -> {
            float[] newColor = {component.color.x, component.color.y, component.color.z, component.color.w};
            ImGui.colorEdit4("Color", newColor);
            component.color.x = newColor[0];
            component.color.y = newColor[1];
            component.color.z = newColor[2];
            component.color.w = newColor[3];

            float[] newThickness = new float[]{component.thickness};
            ImGui.dragFloat("Density", newThickness, 0.025f, 0.0f, 1.0f);
            component.thickness = newThickness[0];

            float[] newFade = new float[]{component.fade};
            ImGui.dragFloat("Fade", newFade, 0.00025f, 0.0f, 1.0f);
            component.fade = newFade[0];
        });

        drawComponent(Rigidbody2DComponent.class, "Rigidbody 2D", entity, component -> {
            String[] bodyTypeStrings = new String[]{"Static", "Dynamic", "Kinmatic"};
            String currentbodyTypeString = bodyTypeStrings[component.type.ordinal()];

            if (ImGui.beginCombo("Body Type", currentbodyTypeString)) {
                for (int i = 0; i < 3; i++) {
                    boolean isSelected = currentbodyTypeString.equals(bodyTypeStrings[i]);
                    if (ImGui.selectable(bodyTypeStrings[i], isSelected)) {
                        currentbodyTypeString = bodyTypeStrings[i];
                        component.type = Rigidbody2DComponent.BodyType.values()[i];
                    }

                    if (isSelected) {
                        ImGui.setItemDefaultFocus();
                    }
                }
                ImGui.endCombo();
            }
            if (ImGui.checkbox("Fixed Rotation", component.fixedRotation)) {
                component.fixedRotation = !component.fixedRotation;
            }
        });

        drawComponent(BoxCollider2DComponent.class, "Box Collider 2D", entity, component -> {
            float[] newOffset = new float[]{component.offset.x, component.offset.y};
            ImGui.dragFloat2("Offset", newOffset);
            component.offset.x = newOffset[0];
            component.offset.y = newOffset[1];

            float[] newSize = new float[]{component.size.x, component.size.y};
            ImGui.dragFloat2("Size", newSize);
            component.size.x = newSize[0];
            component.size.y = newSize[1];

            float[] newDensity = new float[]{component.density};
            ImGui.dragFloat("Density", newDensity, 0.01f, 0.0f, 1.0f);
            component.density = newDensity[0];

            float[] newFriction = new float[]{component.friction};
            ImGui.dragFloat("Friction", newFriction, 0.01f, 0.0f, 1.0f);
            component.friction = newFriction[0];

            float[] newRestitution = new float[]{component.restitution};
            ImGui.dragFloat("Restitution", newRestitution, 0.01f, 0.0f, 1.0f);
            component.restitution = newRestitution[0];

            float[] newRestitutionThreshold = new float[]{component.restitutionThreshold};
            ImGui.dragFloat("Restitution Threshold", newRestitutionThreshold, 0.01f, 0.0f, 1.0f);
            component.restitutionThreshold = newRestitutionThreshold[0];
        });

        drawComponent(CircleCollider2DComponent.class, "Circle Collider 2D", entity, component -> {
            float[] newOffset = new float[]{component.offset.x, component.offset.y};
            ImGui.dragFloat2("Offset", newOffset);
            component.offset.x = newOffset[0];
            component.offset.y = newOffset[1];

            float[] newRadius = new float[]{component.radius};
            ImGui.dragFloat("Radius", newRadius);
            component.radius = newRadius[0];

            float[] newDensity = new float[]{component.density};
            ImGui.dragFloat("Density", newDensity, 0.01f, 0.0f, 1.0f);
            component.density = newDensity[0];

            float[] newFriction = new float[]{component.friction};
            ImGui.dragFloat("Friction", newFriction, 0.01f, 0.0f, 1.0f);
            component.friction = newFriction[0];

            float[] newRestitution = new float[]{component.restitution};
            ImGui.dragFloat("Restitution", newRestitution, 0.01f, 0.0f, 1.0f);
            component.restitution = newRestitution[0];

            float[] newRestitutionThreshold = new float[]{component.restitutionThreshold};
            ImGui.dragFloat("Restitution Threshold", newRestitutionThreshold, 0.01f, 0.0f, 1.0f);
            component.restitutionThreshold = newRestitutionThreshold[0];
        });
    }

    private <T> void displayAddComponentEntry(Class<T> componentType, String entryName) {
        if (!selectionContext.hasComponent(componentType)) {
            if (ImGui.menuItem(entryName)) {
                try {
                    Constructor<?> constructor = componentType.getConstructor();
                    constructor.setAccessible(true);
                    selectionContext.addComponent(constructor.newInstance());
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                ImGui.closeCurrentPopup();
            }
        }
    }
}
