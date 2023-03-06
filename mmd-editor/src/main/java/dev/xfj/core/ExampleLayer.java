package dev.xfj.core;

import dev.xfj.core.events.Event;
import org.slf4j.Logger;

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
        logger.debug("Example Layer onUpdate()");
    }

    @Override
    public void onEvent(Event event) {
        logger.debug(event.toString());
    }
}
