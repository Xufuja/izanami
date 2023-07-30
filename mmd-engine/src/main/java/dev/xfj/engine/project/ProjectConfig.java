package dev.xfj.engine.project;

import java.nio.file.Path;

public class ProjectConfig {
    public String name = "Sandbox";
    public Path startScene = Path.of("scenes/Physics2D.scene");
    public Path assetDirectory = Path.of("assets");
    public Path scriptModulePath = Path.of("scripts/dist/sandbox.mjs");
}
