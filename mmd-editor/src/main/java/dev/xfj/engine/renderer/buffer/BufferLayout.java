package dev.xfj.engine.renderer.buffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BufferLayout implements Iterable<BufferElement> {

    private final List<BufferElement> elements;
    private int stride;

    public BufferLayout() {
        this.elements = new ArrayList<>();
        this.stride = 0;
    }
    public BufferLayout(BufferElement... elements) {
        this.elements = Arrays.asList(elements);
        calculateOffsetsAndStride();
    }
    public int getStride() {
        return stride;
    }

    public List<BufferElement> getElements() {
        return elements;
    }

    public Iterator<BufferElement> iterator() {
        return elements.iterator();
    }

    private void calculateOffsetsAndStride() {
        int offset = 0;
        stride = 0;
        for (BufferElement element : elements) {
            element.setOffset(offset);
            offset += element.getSize();
            stride += element.getSize();
        }
    }
}
