package dev.xfj.engine.renderer.texture;

import java.nio.ByteBuffer;
public interface Texture {
    TextureSpecification getSpecification();
    int getWidth();

    int getHeight();

    int getRendererId();

    String getPath();

    void setData(ByteBuffer data, int size);
    boolean equals(Texture other);

    default void bind() {
        bind(0);
    }
    void bind(int slot);

    boolean isLoaded();

}
