package dev.xfj.particlesystem;

import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.renderer.Renderer2D;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystem {
    private final List<Particle> particlePool;
    private int poolIndex;

    public ParticleSystem() {
        this.particlePool = new ArrayList<>();
        this.poolIndex = 999;
        while (this.particlePool.size() < 1000) {
            this.particlePool.add(new Particle());
        }
    }

    public void emit(ParticleProps particleProps) {
        Particle particle = particlePool.get(poolIndex);
        particle.active = true;
        particle.position = particleProps.position;
        particle.rotation = (float) (Math.random() * 2.0f * Math.PI);

        particle.velocity = particleProps.velocity;
        particle.velocity.x = particleProps.velocityVariation.x * ((float) Math.random() - 0.5f);
        particle.velocity.y = particleProps.velocityVariation.y * ((float) Math.random() - 0.5f);

        particle.colorBegin = particleProps.colorBegin;
        particle.colorEnd = particleProps.colorEnd;

        particle.sizeBegin = particleProps.sizeBegin + particleProps.sizeVariation * ((float) Math.random() - 0.5f);
        particle.sizeEnd = particleProps.sizeEnd;

        particle.lifeTime = particleProps.lifeTime;
        particle.lifeRemaining = particleProps.lifeTime;

        poolIndex = --poolIndex & particlePool.size();

    }

    public void onUpdate(TimeStep ts) {
        for (Particle particle : particlePool) {
            if (!particle.active) {
                continue;
            }
            if (particle.lifeRemaining <= 0.0f) {
                particle.active = false;
                continue;
            }
            particle.lifeRemaining -= ts.getTime();
            particle.position.add(particle.velocity.mul(ts.getTime()));
            particle.rotation += 0.01f * ts.getTime();
        }
    }

    public void onRender() {
        for (Particle particle : particlePool) {
            if (!particle.active) {
                continue;
            }
            float life = particle.lifeRemaining / particle.lifeTime;
            Vector4f color = particle.colorEnd.lerp(particle.colorBegin, life);
            color.w = color.w * life;

            float size = Math.fma(1.0f - life, particle.sizeBegin, life * particle.sizeEnd);
            Renderer2D.drawRotatedQuad(particle.position, new Vector2f(size, size), particle.rotation, color);

        }
    }
}
