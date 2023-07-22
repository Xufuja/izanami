package dev.xfj.engine.scene.components;

public class TagComponent implements Component {
    public String tag;

    public TagComponent() {
        this("");
    }

    public TagComponent(String tag) {
        this.tag = tag;
    }

}
