package dev.xfj.engine.renderer;

import java.nio.ByteBuffer;

public interface Texture {
    int getWidth();

    int getHeight();

    int getRendererId();

    void setData(ByteBuffer data, int size);
    boolean equals(Texture other);

    default void bind() {
        bind(0);
    }
    void bind(int slot);

    boolean isLoaded();

}
