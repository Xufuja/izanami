package dev.xfj.core;

import dev.xfj.core.events.Event;
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
            logger.debug("Tab is pressed!");
        }
    }

    @Override
    public void onEvent(Event event) {
        //logger.debug(event.toString());
    }
}
