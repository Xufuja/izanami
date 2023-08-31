package dev.xfj.engine.core.window;

public class WindowProps {
    public String title;
    public int width;
    public int height;


    public WindowProps(String name) {
        this(name, 1600, 900);
    }

    public WindowProps(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }
}
