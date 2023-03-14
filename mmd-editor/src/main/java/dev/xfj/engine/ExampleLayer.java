package dev.xfj.engine;

import dev.xfj.engine.event.Event;
import dev.xfj.engine.event.key.KeyPressedEvent;
import imgui.ImGui;

import static dev.xfj.engine.KeyCodes.MMD_KEY_TAB;

public class ExampleLayer extends Layer {

    public ExampleLayer() {
        super("Example Layer");
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onUpdate() {
        if (Input.isKeyPressed(MMD_KEY_TAB)) {
            Log.debug("Tab is pressed (polled)!");
        }
    }

    @Override
    public void onImGuiRender() {
        ImGui.begin("Test");
        ImGui.text("Hello World");
        ImGui.end();
    }

    @Override
    public void onEvent(Event event) {
        if (event.getEventType() == Event.EventType.KeyPressed) {
            KeyPressedEvent testEvent = (KeyPressedEvent) event;
            if (testEvent.getKeyCode() == MMD_KEY_TAB) {
                Log.debug("Tab is pressed (event)!");
            }
            Log.debug(String.format("%1$s", (char) testEvent.getKeyCode()));
        }
    }
}
