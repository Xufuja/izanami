package dev.xfj.engine.renderer.texture;

public class TextureSpecification {
    public int width = 1;
    public int height = 1;
    public ImageFormat format = ImageFormat.RGBA8;
    public boolean generateMips = true;

    public enum ImageFormat
    {
        None,
        R8,
        RGB8,
        RGBA8,
        RGBA32F
    };
}
