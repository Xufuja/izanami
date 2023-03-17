package dev.xfj.engine.renderer;

public interface Texture {
    int getWidth();

    int getHeight();

    void bind();
    void bind(int slot);

}
