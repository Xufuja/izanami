package dev.xfj.engine.renderer;

import dev.xfj.engine.renderer.texture.Texture2D;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.msdfgen.MSDFGenBitmap;
import org.lwjgl.util.msdfgen.MSDFGenTransform;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static org.lwjgl.stb.STBImageWrite.stbi_flip_vertically_on_write;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.msdfgen.MSDFGen.*;
import static org.lwjgl.util.msdfgen.MSDFGenExt.*;

public class Font {
    private static Font defaultFont;
    private final Texture2D atlasTexture;

    public static Texture2D createAndCacheAtlas(String fontName) {
        int dot = fontName.lastIndexOf(".");
        Path path = Path.of(fontName.substring(0, dot) + ".bmp");

        return Texture2D.create(path);
    }

    public Font(String fontName) {
        /*try (MemoryStack stack = stackPush()) {
            PointerBuffer pp = stack.mallocPointer(1);

            if (msdf_ft_init(pp) == MSDF_SUCCESS) {
                long ft = pp.get(0);

                msdf_ft_load_font(ft, "assets/fonts/opensans/OpenSans-Regular.ttf", pp);
                long font = pp.get(0);

                msdf_ft_font_load_glyph(font, 'C', MSDF_FONT_SCALING_EM_NORMALIZED, pp);
                long shape = pp.get(0);

                msdf_shape_normalize(shape);
                msdf_shape_edge_colors_simple(shape, 3.0);

                MSDFGenBitmap bitmap = MSDFGenBitmap.calloc(stack);
                msdf_bitmap_alloc(MSDF_BITMAP_TYPE_MSDF, 32, 32, bitmap);

                msdf_generate_msdf(bitmap, shape, MSDFGenTransform.calloc(stack)
                        .scale(it -> it
                                .x(32.0)
                                .y(32.0))
                        .translation(it -> it
                                .x(0.125)
                                .y(0.125))
                        .distance_mapping(it -> it.
                                lower(-0.5 * 0.125)
                                .upper(0.5 * 0.125))
                );

                IntBuffer pi = stack.mallocInt(1);
                msdf_bitmap_get_channel_count(bitmap, pi);
                int channelCount = pi.get(0);

                ByteBuffer pixels = getBitmapU8(stack, bitmap, channelCount);

                stbi_flip_vertically_on_write(true);
                stbi_write_png("output.png", bitmap.width(), bitmap.height(), channelCount, pixels, 0);

                memFree(pixels);

                nmsdf_ft_font_destroy(font);
                msdf_ft_deinit(ft);
            }
        }*/

        //There do not appear to be Java bindings for msdf-atlas-gen
        //I would skip this entirely if I could, but this is not an option
        //So just getting the C++ version to generate the atlas and load it here for now
        atlasTexture = createAndCacheAtlas(fontName);
    }


    public Texture2D getAtlasTexture() {
        return atlasTexture;
    }

    public static Font getDefault() {
        if (defaultFont == null) {
            defaultFont = new Font("assets/fonts/opensans/OpenSans-Regular.ttf");
        }

        return defaultFont;
    }

    private static ByteBuffer getBitmapU8(MemoryStack stack, MSDFGenBitmap bitmap, int channelCount) {
        PointerBuffer pp = stack.mallocPointer(1);

        msdf_bitmap_get_byte_size(bitmap, pp);
        long byteSize = pp.get(0);

        msdf_bitmap_get_pixels(bitmap, pp);
        FloatBuffer pixels = memFloatBuffer(pp.get(0), (int)byteSize >> 2);

        ByteBuffer data = memAlloc(bitmap.width() * bitmap.height() * channelCount);
        for (int y = 0; y < bitmap.height(); y++) {
            for (int x = 0; x < bitmap.width(); x++) {
                int index = (y * bitmap.width() + x) * channelCount;
                for (int c = 0; c < channelCount; c++) {
                    data.put(index + c, (byte)(255.0f * pixels.get(index + c)));
                }
            }
        }

        return data;
    }
}
