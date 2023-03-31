package dev.xfj.level;

import dev.xfj.Player;
import dev.xfj.engine.core.TimeStep;
import dev.xfj.engine.renderer.Renderer2D;
import dev.xfj.engine.renderer.Texture2D;
import org.joml.*;
import org.joml.Math;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private Player player;
    private boolean gameOver;
    private float pillarTarget;
    private int pillarIndex;
    private Vector3f pillarHSV;
    private List<Pillar> pillars;
    private Texture2D triangleTexture;

    public Level() {
        this.player = new Player();
        this.gameOver = false;
        this.pillarTarget = 30.0f;
        this.pillarIndex = 0;
        this.pillarHSV = new Vector3f(0.0f, 0.8f, 0.8f);
        this.pillars = new ArrayList<>();

    }

    private static Vector4f hsvToRgb(Vector3f hsv) {
        int H = (int) (hsv.x * 360.0f);
        double S = hsv.y;
        double V = hsv.z;

        double C = S * V;
        double X = C * (1 - Math.abs(((H / 60.0) % 2) - 1));
        double m = V - C;
        double Rs, Gs, Bs;

        if (H >= 0 && H < 60) {
            Rs = C;
            Gs = X;
            Bs = 0;
        } else if (H >= 60 && H < 120) {
            Rs = X;
            Gs = C;
            Bs = 0;
        } else if (H >= 120 && H < 180) {
            Rs = 0;
            Gs = C;
            Bs = X;
        } else if (H >= 180 && H < 240) {
            Rs = 0;
            Gs = X;
            Bs = C;
        } else if (H >= 240 && H < 300) {
            Rs = X;
            Gs = 0;
            Bs = C;
        } else {
            Rs = C;
            Gs = 0;
            Bs = X;
        }
        return new Vector4f((float) (Rs + m), (float) (Gs + m), (float) (Bs + m), 1.0f);
    }

    private static boolean pointInTri(Vector2f p, Vector2f p0, Vector2f p1, Vector2f p2) {
        float s = p0.y * p2.x - p0.x * p2.y + (p2.y - p0.y) * p.x + (p0.x - p2.x) * p.y;
        float t = p0.x * p1.y - p0.y * p1.x + (p0.y - p1.y) * p.x + (p1.x - p0.x) * p.y;

        if ((s < 0) != (t < 0))
            return false;

        float A = -p1.y * p2.x + p0.y * (p2.x - p1.x) + p0.x * (p1.y - p2.y) + p1.x * p2.y;

        return A < 0 ?
                (s <= 0 && s + t >= A) :
                (s >= 0 && s + t <= A);
    }

    public void init() {
        this.triangleTexture = Texture2D.create(Path.of("assets/textures/Triangle.png"));
        this.player.loadAssets();
        while (pillars.size() < 5) {
            pillars.add(new Pillar());
        }
        for (int i = 0; i < 5; i++) {
            createPillar(i, i * 10.0f);
        }
    }

    public void onUpdate(TimeStep ts) {
        player.onUpdate(ts);

        if (collisionTest()) {
            gameOver();
            return;
        }

        pillarHSV.x += 0.01 * ts.getTime();

        if (pillarHSV.x > 1.0f) {
            pillarHSV.x = 0.0f;
        }

        if (player.getPosition().x > pillarTarget) {
            createPillar(pillarIndex, pillarTarget + 20.0f);
            pillarIndex = ++pillarIndex % pillars.size();
            pillarTarget += 10.0f;
        }
    }

    public void onRender() {
        Vector2f playerPos = player.getPosition();
        Vector4f color = hsvToRgb(pillarHSV);

        Renderer2D.drawQuad(new Vector3f(playerPos.x, 0.0f, -0.8f), new Vector2f(50.0f, 50.0f), new Vector4f(0.3f, 0.3f, 0.3f, 1.0f));
        Renderer2D.drawQuad(new Vector2f(playerPos.x, 34.0f), new Vector2f(50.0f, 50.0f), color);
        Renderer2D.drawQuad(new Vector2f(playerPos.x, -34.0f), new Vector2f(50.0f, 50.0f), color);

        for (Pillar pillar : pillars) {
            Renderer2D.drawRotatedQuad(pillar.topPosition, pillar.topScale, Math.toRadians(180.0f), triangleTexture, 1.0f, color);
            Renderer2D.drawRotatedQuad(pillar.bottomPosition, pillar.bottomScale, Math.toRadians(0.0f), triangleTexture, 1.0f, color);
        }
        player.onRender();

    }

    public void onImGuiRender() {
        player.onImGuiRender();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void reset() {
        gameOver = false;
        player.reset();
        pillarTarget = 30.0f;
        pillarIndex = 0;
        for (int i = 0; i < 5; i++) {
            createPillar(i, i * 10.0f);
        }
    }

    public Player getPlayer() {
        return player;
    }

    private void createPillar(int index, float offset) {
        Pillar pillar = pillars.get(index);
        pillar.topPosition.x = offset;
        pillar.bottomPosition.x = offset;
        pillar.topPosition.z = index * 0.1f - 0.5f;
        pillar.bottomPosition.z = index * 0.1f - 0.5f + 0.05f;

        float center = (float) (Math.random() * 35.0f - 17.5f);
        float gap = 2.0f + (float) Math.random() * 5.0f;

        pillar.topPosition.y = 10.0f - ((10.0f - center) * 0.2f) + gap * 0.5f;
        pillar.bottomPosition.y = -10.0f - ((-10.0f - center) * 0.2f) - gap * 0.5f;
    }

    private boolean collisionTest() {
        if (Math.abs(player.getPosition().y) > 8.5f) {
            return true;
        }

        Vector4f[] playerVertices = new Vector4f[]{
                new Vector4f(-0.5f, -0.5f, 0.0f, 1.0f),
                new Vector4f(0.5f, -0.5f, 0.0f, 1.0f),
                new Vector4f(0.5f, 0.5f, 0.0f, 1.0f),
                new Vector4f(-0.5f, 0.5f, 0.0f, 1.0f)
        };

        Vector2f pos = player.getPosition();

        Vector4f[] playerTransformedVerts = new Vector4f[4];

        for (int i = 0; i < 4; i++) {
            playerTransformedVerts[i] = new Matrix4f().translate(pos.x, pos.y, 0.0f).mul(new Matrix4f().rotate(Math.toRadians(player.getRotation()), 0.0f, 0.0f, 1.0f)).mul(new Matrix4f().scale(1.0f, 1.3f, 1.0f)).transform(playerVertices[i]);
        }

        Vector4f[] pillarVertices = new Vector4f[]{
                new Vector4f(-0.5f + 0.1f, -0.5f + 0.1f, 0.0f, 1.0f),
                new Vector4f(0.5f - 0.1f, -0.5f + 0.1f, 0.0f, 1.0f),
                new Vector4f(0.0f + 0.0f, 0.5f - 0.1f, 0.0f, 1.0f)
        };

        for (Pillar p : pillars) {
            Vector2f[] tri = new Vector2f[3];
            for (int i = 0; i < 3; i++) {
                Vector4f temp = new Matrix4f().translate(p.topPosition.x, p.topPosition.y, 0.0f).mul(new Matrix4f().rotate(Math.toRadians(180.0f), 0.0f, 0.0f, 1.0f)).mul(new Matrix4f().scale(p.topScale.x, p.topScale.y, 1.0f)).transform(pillarVertices[i], new Vector4f());
                tri[i] = new Vector2f(temp.x, temp.y);
            }

            for (Vector4f vert : playerTransformedVerts) {
                if (pointInTri(new Vector2f(vert.x, vert.y), tri[0], tri[1], tri[2]))
                    return true;
            }

            for (int i = 0; i < 3; i++) {
                Vector4f temp = new Matrix4f().translate(p.bottomPosition.x, p.bottomPosition.y, 0.0f).mul(new Matrix4f().scale(p.bottomScale.x, p.bottomScale.y, 1.0f)).transform(pillarVertices[i], new Vector4f());
                tri[i] = new Vector2f(temp.x, temp.y);
            }

            for (Vector4f vert : playerTransformedVerts) {
                if (pointInTri(new Vector2f(vert.x, vert.y), tri[0], tri[1], tri[2]))
                    return true;
            }

        }
        return false;
    }

    private void gameOver() {
        gameOver = true;
    }

}
