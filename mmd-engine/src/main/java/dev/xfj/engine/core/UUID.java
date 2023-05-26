package dev.xfj.engine.core;

import java.util.Objects;
import java.util.Random;

public class UUID {
    private final long uuid;

    public UUID() {
        this.uuid = new Random().nextLong();
    }

    public UUID(long uuid) {
        this.uuid = uuid;
    }

    public long getUUID() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UUID id = (UUID) o;
        return uuid == id.uuid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
