package dev.xfj.window;

public class WindowProps {
    String title;
    int width;
    int height;

    public WindowProps() {
        this("MMD Editor", 1280, 720);
    }

    public WindowProps(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }
}
