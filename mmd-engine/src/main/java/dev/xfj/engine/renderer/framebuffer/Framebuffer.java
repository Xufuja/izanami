package dev.xfj.engine.renderer.framebuffer;

import dev.xfj.engine.core.Log;
import dev.xfj.engine.renderer.Renderer;
import dev.xfj.platform.opengl.OpenGLFramebuffer;

public interface Framebuffer {
    static Framebuffer create(FramebufferSpecification spec) {
        return switch (Renderer.getAPI()) {
            case None -> {
                Log.error("RendererAPI None is not supported!");
                yield null;
            }
            case OpenGL -> new OpenGLFramebuffer(spec);
            default -> {
                Log.error("Unknown RendererAPI!");
                yield null;
            }
        };
    }

    enum FramebufferTextureFormat {
        None,
        RGBA8,
        DEPTH24STENCIL8,
        Depth
    }

    void bind();

    void unbind();

    void resize(int width, int height);

    default int getColorAttachmentRendererId() {
        return getColorAttachmentRendererId(0);
    }

    int getColorAttachmentRendererId(int index);

    FramebufferSpecification getSpecification();

}
