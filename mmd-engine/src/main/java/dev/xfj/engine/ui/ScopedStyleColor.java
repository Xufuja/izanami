package dev.xfj.engine.ui;

import imgui.ImGui;
import imgui.ImVec4;

public class ScopedStyleColor {
    private final boolean set;

    public ScopedStyleColor(int idx, ImVec4 color) {
        this(idx, color, true);
    }

    public ScopedStyleColor(int idx, ImVec4 color, boolean predicate) {
        this.set = predicate;

        if (predicate) {
            ImGui.pushStyleColor(idx, color.x, color.y, color.z, color.w);
        }
    }
    public void popStyleColor() {
        if (set) {
            ImGui.popStyleColor();
        }
    }
}
