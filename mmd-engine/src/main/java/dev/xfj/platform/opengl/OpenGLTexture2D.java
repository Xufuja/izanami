package dev.xfj.platform.opengl;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.Texture;
import dev.xfj.engine.renderer.Texture2D;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL45;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;

public class OpenGLTexture2D extends Texture2D {
    private final Path path;
    private final int width;
    private final int height;
    private final int rendererId;
    private final int internalFormat;
    private final int dataFormat;
    private boolean isLoaded;

    public OpenGLTexture2D(int width, int height) {
        this.path = null;
        this.width = width;
        this.height = height;

        this.internalFormat = GL_RGBA8;
        this.dataFormat = GL_RGBA;

        this.rendererId = GL45.glCreateTextures(GL45.GL_TEXTURE_2D);
        GL45.glTextureStorage2D(this.rendererId, 1, this.internalFormat, this.width, this.height);

        GL45.glTextureParameteri(this.rendererId, GL45.GL_TEXTURE_MIN_FILTER, GL45.GL_LINEAR);
        GL45.glTextureParameteri(this.rendererId, GL45.GL_TEXTURE_MAG_FILTER, GL45.GL_LINEAR);

        GL45.glTextureParameteri(this.rendererId, GL45.GL_TEXTURE_WRAP_S, GL45.GL_REPEAT);
        GL45.glTextureParameteri(this.rendererId, GL45.GL_TEXTURE_WRAP_T, GL45.GL_REPEAT);

        this.isLoaded = false;
    }

    public OpenGLTexture2D(Path path) {
        this.path = path;

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer data = STBImage.stbi_load(path.normalize().toString(), width, height, channels, 0);

        if (data != null && data.hasRemaining()) {
            this.isLoaded = true;

            this.width = width.get(0);
            this.height = height.get(0);

            int internalFormat = 0;
            int dataFormat = 0;

            if (channels.get(0) == 4) {
                internalFormat = GL_RGBA8;
                dataFormat = GL_RGBA;
            } else if (channels.get(0) == 3) {
                internalFormat = GL45.GL_RGB8;
                dataFormat = GL45.GL_RGB;
            }

            this.internalFormat = internalFormat;
            this.dataFormat = dataFormat;
            //Some sort of exception HZ_CORE_ASSERT(internalFormat & dataFormat, "Format not supported!");

            this.rendererId = GL45.glCreateTextures(GL45.GL_TEXTURE_2D);
            GL45.glTextureStorage2D(this.rendererId, 1, internalFormat, this.width, this.height);

            GL45.glTextureParameteri(this.rendererId, GL45.GL_TEXTURE_MIN_FILTER, GL45.GL_LINEAR);
            GL45.glTextureParameteri(this.rendererId, GL45.GL_TEXTURE_MAG_FILTER, GL45.GL_LINEAR);

            GL45.glTextureParameteri(this.rendererId, GL45.GL_TEXTURE_WRAP_S, GL45.GL_REPEAT);
            GL45.glTextureParameteri(this.rendererId, GL45.GL_TEXTURE_WRAP_T, GL45.GL_REPEAT);

            GL45.glTextureSubImage2D(this.rendererId, 0, 0, 0, this.width, this.height, dataFormat, GL45.GL_UNSIGNED_BYTE, data);

            STBImage.stbi_image_free(data);
        } else {
            this.width = 0;
            this.height = 0;
            this.rendererId = 0;
            this.internalFormat = 0;
            this.dataFormat = 0;
        }
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
    public int getRendererId() {
        return this.rendererId;
    }


    @Override
    public void setData(ByteBuffer data, int size) {
        int bpp = this.dataFormat == GL_RGBA ? 4 : 3;
        //Some sort of exception
        if (size != this.width * this.width * bpp) {
            Log.error("Data must be entire texture!");
        }
        GL45.glTextureSubImage2D(this.rendererId, 0, 0, 0, this.width, this.height, this.dataFormat, GL45.GL_UNSIGNED_BYTE, data);
    }

    @Override
    public boolean equals(Texture other) {
        return rendererId == ((OpenGLTexture2D) other).rendererId;
    }

    @Override
    public void bind(int slot) {
        GL45.glBindTextureUnit(slot, this.rendererId);
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }
}
