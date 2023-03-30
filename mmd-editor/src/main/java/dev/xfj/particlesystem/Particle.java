package dev.xfj.particlesystem;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Particle {
    public Vector2f position;
    public Vector2f velocity;
    public Vector4f colorBegin;
    public Vector4f colorEnd;
    public float rotation = 0.0f;
    public float sizeBegin;
    public float sizeEnd;
    public float lifeTime = 1.0f;
    public float lifeRemaining = 0.0f;
    public boolean active = false;
}
