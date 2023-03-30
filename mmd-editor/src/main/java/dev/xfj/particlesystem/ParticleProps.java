package dev.xfj.particlesystem;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class ParticleProps {
    public Vector2f position;
    public Vector2f velocity;
    public Vector2f velocityVariation;
    public Vector4f colorBegin;
    public Vector4f colorEnd;
    public float sizeBegin;
    public float sizeEnd;
    public float sizeVariation;
    public float lifeTime = 1.0f;
}
