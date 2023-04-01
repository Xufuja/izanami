package dev.xfj;

import dev.xfj.engine.core.Input;
import dev.xfj.engine.core.Log;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.renderer.Renderer2D;
import dev.xfj.engine.renderer.Texture2D;
import dev.xfj.particlesystem.ParticleProps;
import dev.xfj.particlesystem.ParticleSystem;
import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.file.Path;

import static dev.xfj.engine.core.KeyCodes.MMD_KEY_SPACE;

public class Player {
    private Vector2f position;
    private Vector2f velocity;
    private float enginePower;
    private final float gravity;
    private float time;
    private final float smokeEmitInterval;
    private float smokeNextEmitTime;
    private final ParticleProps smokeParticle;
    private final ParticleProps engineParticle;
    private final ParticleSystem particleSystem;
    private Texture2D shipTexture;

    public Player() {
        this.position = new Vector2f(-10.0f, 0.0f);
        this.velocity = new Vector2f(5.0f, 0.0f);
        this.enginePower = 0.5f;
        this.gravity = 0.4f;
        this.time = 0.0f;
        this.smokeEmitInterval = 0.4f;
        this.smokeNextEmitTime = this.smokeEmitInterval;
        this.particleSystem = new ParticleSystem();
        this.smokeParticle = new ParticleProps();
        this.engineParticle = new ParticleProps();

        this.smokeParticle.position = new Vector2f(0.0f, 0.0f);
        this.smokeParticle.velocity = new Vector2f(-2.0f, 0.0f);
        this.smokeParticle.velocityVariation = new Vector2f(4.0f, 2.0f);
        this.smokeParticle.sizeBegin = 0.35f;
        this.smokeParticle.sizeEnd = 0.0f;
        this.smokeParticle.sizeVariation = 0.15f;
        this.smokeParticle.colorBegin = new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);
        this.smokeParticle.colorEnd = new Vector4f(0.6f, 0.6f, 0.6f, 1.0f);
        this.smokeParticle.lifeTime = 4.0f;

        this.engineParticle.position = new Vector2f(0.0f, 0.0f);
        this.engineParticle.velocity = new Vector2f(-2.0f, 0.0f);
        this.engineParticle.velocityVariation = new Vector2f(3.0f, 1.0f);
        this.engineParticle.sizeBegin = 0.5f;
        this.engineParticle.sizeEnd = 0.0f;
        this.engineParticle.sizeVariation = 0.3f;
        this.engineParticle.colorBegin = new Vector4f(254 / 255.0f, 109 / 255.0f, 41 / 255.0f, 1.0f);
        this.engineParticle.colorEnd = new Vector4f(254 / 255.0f, 212 / 255.0f, 123 / 255.0f, 1.0f);
        this.engineParticle.lifeTime = 1.0f;
    }


    public void loadAssets() {
        shipTexture = Texture2D.create(Path.of("assets/textures/Ship.png"));
    }

    public void onUpdate(TimeStep ts) {
        time += ts.getTime();

        if (Input.isKeyPressed(MMD_KEY_SPACE)) {
            velocity.y += enginePower;

            if (velocity.y < 0.0f) {
                velocity.y += enginePower * 2.0f;
            }

            Vector2f emissionPoint = new Vector2f(0.0f, -0.6f);
            float rotation = (float) Math.toRadians(getRotation());
            Vector4f rotated = new Vector4f(emissionPoint.x, emissionPoint.y, 0.0f, 1.0f).mul(new Matrix4f().rotate(rotation, new Vector3f(0.0f, 0.0f, 1.0f)));
            engineParticle.position = position.add(new Vector2f(rotated.x, rotated.y));
            engineParticle.velocity.y = -velocity.y * 0.2f - 0.2f;
            particleSystem.emit(engineParticle);
        } else {
            velocity.y -= gravity;
        }

        velocity.y = Math.max(-20.0f, Math.min(velocity.y, 20.0f));

        position.add(velocity.mul(ts.getTime(), new Vector2f()));

        if (time > smokeNextEmitTime) {
            smokeParticle.position.set(position);
            particleSystem.emit(smokeParticle);
            smokeNextEmitTime += smokeEmitInterval;
        }

        particleSystem.onUpdate(ts);
    }

    public void onRender() {
        particleSystem.onRender();
        Renderer2D.drawRotatedQuad(new Vector3f(position.x, position.y, 0.5f), new Vector2f(1.0f, 1.3f), (float) Math.toRadians(getRotation()), shipTexture);
    }

    public void onImGuiRender() {
        float[] newEngine = {0.1f};
        ImGui.dragFloat("Engine Power", newEngine);
        enginePower = newEngine[0];

        float[] newGravity = {0.1f};
        ImGui.dragFloat("Gravity", newGravity);
        enginePower = newGravity[0];

    }

    public void reset() {
        position = new Vector2f(-10.0f, 0.0f);
        velocity = new Vector2f(5.0f, 0.0f);
    }

    public float getRotation() {
        return velocity.y * 4.0f - 90.0f;
    }

    public Vector2f getPosition() {
        return position;
    }

    public int getScore() {
        return (int) ((position.x + 10.0f) / 10.0f);
    }
}
