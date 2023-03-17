package dev.xfj.platform.opengl;

import dev.xfj.engine.renderer.Texture2D;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL45;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class OpenGLTexture2D extends Texture2D {
    private final String path;
    private final int width;
    private final int height;
    private final int renderId;

    public OpenGLTexture2D(String path) {
        this.path = path;

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer data = STBImage.stbi_load(path, width, height, channels, 0);
        //Some sort exception if the image cannot be loaded
        this.width = width.get(0);
        this.height = height.get(0);

        this.renderId = GL45.glCreateTextures(GL45.GL_TEXTURE_2D);
        GL45.glTextureStorage2D(this.renderId, 1, GL45.GL_RGB8, this.width, this.height);

        GL45.glTextureParameteri(this.renderId, GL45.GL_TEXTURE_MIN_FILTER, GL45.GL_LINEAR);
        GL45.glTextureParameteri(this.renderId, GL45.GL_TEXTURE_MAG_FILTER, GL45.GL_NEAREST);

        GL45.glTextureSubImage2D(this.renderId, 0, 0, 0, this.width, this.height, GL45.GL_RGB, GL45.GL_UNSIGNED_BYTE, data);

        STBImage.stbi_image_free(data);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void bind(int slot) {
        GL45.glBindTextureUnit(slot, this.renderId);
    }

    public void bind() {
        GL45.glBindTextureUnit(0, this.renderId);
    }
}
