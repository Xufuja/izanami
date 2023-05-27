package dev.xfj.engine.scene.components;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformComponent implements Component {
    public Vector3f translation;
    public Vector3f rotation;
    public Vector3f scale;

    public TransformComponent() {
        this(new Vector3f(0.0f, 0.0f, 0.0f));
    }

    public TransformComponent(Vector3f translation) {
        this.translation = translation;
        this.rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        this.scale = new Vector3f(1.0f, 1.0f, 1.0f);
    }

    public TransformComponent(TransformComponent other) {
        this.translation = new Vector3f(other.translation);
        this.rotation = new Vector3f(other.rotation);
        this.scale = new Vector3f(other.scale);
    }

    public Matrix4f getTransform() {
        Matrix4f rotation = new Matrix4f().rotate(new Quaternionf().rotateXYZ(this.rotation.x, this.rotation.y, this.rotation.z));
        return new Matrix4f().translate(translation).mul(rotation).mul(new Matrix4f().scale(scale));
    }

}
