package dev.xfj.platform.opengl;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.framebuffer.Framebuffer;
import dev.xfj.engine.renderer.framebuffer.FramebufferSpecification;
import org.lwjgl.opengl.GL45;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLFramebuffer implements Framebuffer {
    private int renderId;
    private final int[] colorAttachment;
    private final int[] depthAttachment;
    private final FramebufferSpecification specification;

    public OpenGLFramebuffer(FramebufferSpecification specification) {
        this.specification = specification;
        this.colorAttachment = new int[1];
        this.depthAttachment = new int[1];
        invalidate();
    }

    public void invalidate() {
        renderId = GL45.glCreateFramebuffers();
        GL45.glBindFramebuffer(GL_FRAMEBUFFER, renderId);

        GL45.glCreateTextures(GL_TEXTURE_2D, colorAttachment);
        GL45.glBindTexture(GL_TEXTURE_2D, colorAttachment[0]);
        GL45.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, specification.width, specification.height, 0, GL_RGBA, GL_UNSIGNED_INT, NULL);
        GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        GL45.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachment[0], 0);

        GL45.glCreateTextures(GL_TEXTURE_2D, depthAttachment);
        GL45.glBindTexture(GL_TEXTURE_2D, depthAttachment[0]);
        GL45.glTexStorage2D(GL_TEXTURE_2D, 1, GL_DEPTH24_STENCIL8, specification.width, specification.height);
        //GL45.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, specification.width, specification.height, 0, GL45.GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, NULL);
        GL45.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthAttachment[0], 0);

        //Some sort of exception HZ_CORE_ASSERT(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE, "Framebuffer is incomplete!");
        if (GL45.glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Log.error("Framebuffer is incomplete!");
        }

        GL45.glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    @Override
    public void bind() {
        GL45.glBindFramebuffer(GL_FRAMEBUFFER, renderId);
    }

    @Override
    public void unbind() {
        GL45.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public int getColorAttachmentRendererId() {
        return this.colorAttachment[0];
    }

    @Override
    public FramebufferSpecification getSpecification() {
        return this.specification;
    }
}
