package dev.xfj.engine.renderer.renderer2d;

public class Statistics {
    public int drawCalls = 0;
    public int quadCount = 0;

    public int getTotalVertexCount() {
        return quadCount * 4;
    }

    public int getTotalIndexCount() {
        return quadCount * 6;
    }
}
