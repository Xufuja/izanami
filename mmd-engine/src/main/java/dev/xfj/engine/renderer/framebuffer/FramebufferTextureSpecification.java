package dev.xfj.engine.renderer.framebuffer;

public class FramebufferTextureSpecification {
    public Framebuffer.FramebufferTextureFormat textureFormat = Framebuffer.FramebufferTextureFormat.None;

    public FramebufferTextureSpecification(Framebuffer.FramebufferTextureFormat textureFormat) {
        this.textureFormat = textureFormat;
    }
}
