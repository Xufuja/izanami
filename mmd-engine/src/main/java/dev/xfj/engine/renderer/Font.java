package dev.xfj.engine.renderer;

import dev.xfj.engine.renderer.texture.Texture2D;

import java.nio.file.Path;

public class Font {
    private final Texture2D atlasTexture;

    public static Texture2D createAndCacheAtlas(String fontName) {
        int dot = fontName.lastIndexOf(".");
        Path path = Path.of(fontName.substring(0, dot) + ".bmp");

        return Texture2D.create(path);
    }

    public Font(String font) {
        //There do not appear to be Java bindings for msdf-atlas-gen
        //I would skip this entirely if I could, but this is not an option
        //So just getting the C++ version to generate the atlas and load it here for now
        atlasTexture = createAndCacheAtlas(font);
    }

    public Texture2D getAtlasTexture() {
        return atlasTexture;
    }
}
