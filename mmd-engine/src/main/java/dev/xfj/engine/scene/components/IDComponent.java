package dev.xfj.engine.scene.components;

import dev.xfj.engine.core.UUID;

public class IDComponent implements Component {
    public UUID id;

    public IDComponent() {
        this(new UUID());
    }

    public IDComponent(UUID id) {
        this.id = id;
    }
}
