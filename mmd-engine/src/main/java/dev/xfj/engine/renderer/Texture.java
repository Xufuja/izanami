package dev.xfj.engine.renderer;

import java.nio.ByteBuffer;

public interface Texture {
    int getWidth();

    int getHeight();

    int getRendererId();

    void setData(ByteBuffer data, int size);
    boolean equals(Texture other);

    void bind();
    void bind(int slot);

}
