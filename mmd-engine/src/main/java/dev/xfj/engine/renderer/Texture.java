package dev.xfj.engine.renderer;

import java.nio.ByteBuffer;

public interface Texture {
    int getWidth();

    int getHeight();

    void setData(ByteBuffer data, int size);

    void bind();
    void bind(int slot);

}
