package dev.xfj.core;

import dev.xfj.core.events.Event;
import dev.xfj.core.events.key.KeyPressedEvent;
import imgui.ImGui;
import org.slf4j.Logger;

import static dev.xfj.core.KeyCodes.MMD_KEY_TAB;

public class ExampleLayer extends Layer {
    public static final Logger logger = Log.init(ExampleLayer.class.getSimpleName());

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
            logger.debug("Tab is pressed (polled)!");
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
                logger.debug("Tab is pressed (event)!");
            }
            logger.debug(String.format("%1$s", (char) testEvent.getKeyCode()));
        }
    }
}
