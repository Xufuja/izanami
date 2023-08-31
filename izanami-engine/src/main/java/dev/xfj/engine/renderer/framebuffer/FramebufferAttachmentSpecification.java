package dev.xfj.engine.renderer.framebuffer;

import java.util.List;

public class FramebufferAttachmentSpecification {
    public List<FramebufferTextureSpecification> attachments;

    public FramebufferAttachmentSpecification(List<FramebufferTextureSpecification> attachments) {
        this.attachments = attachments;
    }
}
