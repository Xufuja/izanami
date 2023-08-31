package dev.xfj.engine.imgui;

import imgui.type.ImString;

public class ImGuiStringPayload implements ImGuiPayload<String> {
    private final ImString input;

    public ImGuiStringPayload(String input) {
        this.input = new ImString(input);
    }

    @Override
    public String getData() {
        return input.get();
    }
}
