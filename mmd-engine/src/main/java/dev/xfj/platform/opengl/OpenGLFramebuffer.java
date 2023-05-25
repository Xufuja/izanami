package dev.xfj.platform.opengl;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.framebuffer.Framebuffer;
import dev.xfj.engine.renderer.framebuffer.FramebufferSpecification;
import dev.xfj.engine.renderer.framebuffer.FramebufferTextureSpecification;
import org.lwjgl.opengl.GL45;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLFramebuffer implements Framebuffer {
    private static final int maxFramebufferSize = 8192;
    private int renderId;
    private final FramebufferSpecification specification;
    private List<FramebufferTextureSpecification> colorAttachmentSpecifications;
    private FramebufferTextureSpecification depthAttachmentSpecification;
    private int[] colorAttachments;
    private final int[] depthAttachment;

    public OpenGLFramebuffer(FramebufferSpecification specification) {
        this.renderId = 0;
        this.specification = specification;
        this.colorAttachmentSpecifications = new ArrayList<>();
        this.depthAttachmentSpecification = new FramebufferTextureSpecification(FramebufferTextureFormat.None);
        this.colorAttachments = new int[1];
        this.depthAttachment = new int[1];

        for (var spec : specification.attachments.attachments) {
            if (!OpenGLFramebuffer.isDepthFormat(spec.textureFormat)) {
                colorAttachmentSpecifications.add(spec);
            } else {
                depthAttachmentSpecification = spec;
            }
        }
        invalidate();
    }

    public void invalidate() {
        if (renderId != 0) {
            GL45.glDeleteFramebuffers(renderId);
            GL45.glDeleteTextures(colorAttachments);
            GL45.glDeleteTextures(depthAttachment);

            colorAttachments = new int[1];
            depthAttachment[0] = 0;
        }
        renderId = GL45.glCreateFramebuffers();
        GL45.glBindFramebuffer(GL_FRAMEBUFFER, renderId);

        boolean multisample = specification.samples > 1;

        if (!colorAttachmentSpecifications.isEmpty()) {
            colorAttachments = new int[colorAttachmentSpecifications.size()];

            createTextures(multisample, colorAttachments);

            for (int i = 0; i < colorAttachments.length; i++) {
                bindTexture(multisample, colorAttachments[i]);
                switch (colorAttachmentSpecifications.get(i).textureFormat) {
                    case RGBA8 ->
                            attachColorTexture(colorAttachments[i], specification.samples, GL_RGBA8, GL_RGBA, specification.width, specification.height, i);
                    case RED_INTEGER ->
                            attachColorTexture(colorAttachments[i], specification.samples, GL_R32I, GL_RED_INTEGER, specification.width, specification.height, i);
                }
            }
        }

        if (depthAttachmentSpecification.textureFormat != FramebufferTextureFormat.None) {
            createTextures(multisample, depthAttachment);
            bindTexture(multisample, depthAttachment[0]);

            switch (depthAttachmentSpecification.textureFormat) {
                case Depth, DEPTH24STENCIL8 ->
                        attachDepthTexture(depthAttachment[0], specification.samples, GL_DEPTH24_STENCIL8, GL_DEPTH_STENCIL_ATTACHMENT, specification.width, specification.height);
            }
        }

        if (colorAttachments.length > 1) {
            //Some sort of exception HZ_CORE_ASSERT(m_ColorAttachments.size() <= 4);
            int[] buffers = new int[]{GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3};
            GL45.glDrawBuffers(buffers);
        } else if (colorAttachments.length == 0) {
            GL45.glDrawBuffer(GL_NONE);
        }

        //Some sort of exception HZ_CORE_ASSERT(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE, "Framebuffer is incomplete!");
        if (GL45.glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Log.error("Framebuffer is incomplete!");
        }

        GL45.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void bind() {
        GL45.glBindFramebuffer(GL_FRAMEBUFFER, renderId);
        GL45.glViewport(0, 0, specification.width, specification.height);
    }

    @Override
    public void unbind() {
        GL45.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void resize(int width, int height) {
        if (width == 0 || height == 0 || width > maxFramebufferSize || height > maxFramebufferSize) {
            Log.warn(String.format("Attempted to rezize framebuffer to %1$s, %2$s", width, height));
            return;
        }
        specification.width = width;
        specification.height = height;

        invalidate();
    }

    @Override
    public int readPixel(int attachmentIndex, int x, int y) {
        //Some sort of exception HZ_CORE_ASSERT(attachmentIndex < m_ColorAttachments.size());
        GL45.glReadBuffer(GL_COLOR_ATTACHMENT0 + attachmentIndex);
        int[] pixelData = new int[1];
        GL45.glReadPixels(x, y, 1, 1, GL_RED_INTEGER, GL_INT, pixelData);
        return pixelData[0];
    }

    @Override
    public void clearAttachment(int attachmentIndex, int value) {
        //Some sort of exception HZ_CORE_ASSERT(attachmentIndex < m_ColorAttachments.size());

        FramebufferTextureSpecification spec = colorAttachmentSpecifications.get(attachmentIndex);
        GL45.glClearTexImage(colorAttachments[attachmentIndex], 0, engineFBTextureFormatToGL(spec.textureFormat), GL_INT, new int[]{value});
    }

    @Override
    public int getColorAttachmentRendererId(int index) {
        //Some sort of exception HZ_CORE_ASSERT(index < m_ColorAttachments.size());
        return colorAttachments[index];
    }

    @Override
    public FramebufferSpecification getSpecification() {
        return this.specification;
    }

    public static int textureTarget(boolean multisampled) {
        return multisampled ? GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D;
    }

    public static void createTextures(boolean multisampled, int[] outId) {
        GL45.glCreateTextures(textureTarget(multisampled), outId);
    }

    public static void bindTexture(boolean multisampled, int id) {
        GL45.glBindTexture(textureTarget(multisampled), id);
    }

    public static void attachColorTexture(int id, int samples, int internalFormat, int format, int width, int height, int index) {
        boolean multisampled = samples > 1;
        if (multisampled) {
            GL45.glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, internalFormat, width, height, false);
        } else {
            GL45.glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, NULL);

            GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
            GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        }

        GL45.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + index, textureTarget(multisampled), id, 0);
    }

    public static void attachDepthTexture(int id, int samples, int format, int attachmentType, int width, int height) {
        boolean multisampled = samples > 1;
        if (multisampled) {
            GL45.glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, format, width, height, false);
        } else {
            GL45.glTexStorage2D(GL_TEXTURE_2D, 1, format, width, height);

            GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
            GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            GL45.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        }

        GL45.glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType, textureTarget(multisampled), id, 0);
    }

    public static boolean isDepthFormat(Framebuffer.FramebufferTextureFormat format) {
        return switch (format) {
            //He treats Depth as DEPTH24STENCIL8 so not entirely sure why it is a separate option
            case Depth, DEPTH24STENCIL8 -> true;
            default -> false;
        };
    }

    public static int engineFBTextureFormatToGL(FramebufferTextureFormat format) {
        return switch (format) {
            case RGBA8 -> GL_RGBA8;
            case RED_INTEGER -> GL_RED_INTEGER;
            default -> 0;
        };
    }
}
