package dev.xfj.engine.renderer.framebuffer;

import java.util.ArrayList;

public class FramebufferSpecification {
    public int width = 0;
    public int height = 0;
    public FramebufferAttachmentSpecification attachments = new FramebufferAttachmentSpecification(new ArrayList<>());
    public int samples = 1;
    public boolean swapChainTarget = false;
}
