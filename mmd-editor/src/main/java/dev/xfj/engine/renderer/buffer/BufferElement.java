package dev.xfj.engine.renderer.buffer;

import dev.xfj.engine.Log;

public class BufferElement {
    public String name;
    public ShaderDataType type;
    public int size;
    public int offset;
    boolean normalized;

    public BufferElement(ShaderDataType type, String name) {
        this(type, name, false);
    }

    public BufferElement(ShaderDataType type, String name, boolean normalized) {
        this.name = name;
        this.type = type;
        this.size = shaderDataTypeSize(type);
        this.offset = 0;
        this.normalized = normalized;
    }

    public enum ShaderDataType {
        None, Float, Float2, Float3, Float4, Mat3, Mat4, Int, Int2, Int3, Int4, Bool
    }

    public static int shaderDataTypeSize(ShaderDataType type) {
        return switch (type) {
            case Float, Int -> 4;
            case Float2, Int2 -> 4 * 2;
            case Float3, Int3 -> 4 * 3;
            case Float4, Int4 -> 4 * 4;
            case Mat3 -> 4 * 3 * 3;
            case Mat4 -> 4 * 4 * 4;
            case Bool -> 1;
            default -> {
                Log.error("Unknown ShaderDataType!");
                yield 0;
            }
        };
    }

    public int getComponent() {
        return switch (type) {
            case Float, Int, Bool -> 1;
            case Float2, Int2 -> 2;
            case Float3, Int3 -> 3;
            case Float4, Int4 -> 4;
            case Mat3 -> 3 * 3;
            case Mat4 -> 4 * 4;
            default -> {
                Log.error("Unknown ShaderDataType!");
                yield 0;
            }
        };
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ShaderDataType getType() {
        return type;
    }

    public void setType(ShaderDataType type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isNormalized() {
        return normalized;
    }

    public void setNormalized(boolean normalized) {
        this.normalized = normalized;
    }
}
