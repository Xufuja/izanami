package dev.xfj.engine.scene.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import org.joml.Vector2f;

public class BoxCollider2DComponent implements Component{
    public Vector2f offset = new Vector2f( 0.0f, 0.0f);
    public Vector2f size = new Vector2f( 0.5f, 0.5f );
    public float density = 1.0f;
    public float friction = 0.5f;
    public float restitution = 0.0f;
    public float restitutionThreshold = 0.5f;
    public Fixture runtimeFixture = null;
}
