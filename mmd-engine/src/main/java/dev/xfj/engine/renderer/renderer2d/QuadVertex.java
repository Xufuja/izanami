package dev.xfj.engine.renderer.renderer2d;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class QuadVertex {
    public Vector3f position;
    public Vector4f color;
    public Vector2f texCoord;

    public QuadVertex(Vector3f position, Vector4f color, Vector2f texCoord) {
        this.position = position;
        this.color = color;
        this.texCoord = texCoord;
    }

    public ArrayList<Float> toList() {
        ArrayList<Float> out = new ArrayList<>();
        out.add(position.x);
        out.add(position.y);
        out.add(position.z);
        out.add(color.x );
        out.add(color.y );
        out.add(color.z );
        out.add(color.w );
        out.add(texCoord.x);
        out.add(texCoord.y);
        return out;
    }
    public static int getQuadVertexSize() {
        return 9;
    }
}
