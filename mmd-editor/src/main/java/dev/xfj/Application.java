package dev.xfj;

import dev.xfj.events.Event;
import dev.xfj.events.application.WindowResizeEvent;
import org.slf4j.Logger;


public class Application {
    public static final Logger logger = Log.init(Application.class.getSimpleName());

    public Application() {

    }

    public void run() {
        WindowResizeEvent event = new WindowResizeEvent(1280, 720);
        if (event.isInCategory(Event.EventCategory.EventCategoryApplication)) {
            logger.info("EventCategoryApplication {}", Event.EventCategory.EventCategoryApplication.getCategoryValue());
        }
        if (event.isInCategory(Event.EventCategory.EventCategoryInput)) {
            logger.info("EventCategoryInput {}", Event.EventCategory.EventCategoryInput.getCategoryValue());
        }
    }


}
