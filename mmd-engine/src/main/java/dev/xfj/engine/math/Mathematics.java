package dev.xfj.engine.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Mathematics {
    public boolean decomposeTransform(Matrix4f transform, Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f localMatrix = new Matrix4f(transform);

        if (localMatrix.m33() == 0) {
            return false;
        }

        if (localMatrix.m03() != 0 || localMatrix.m13() != 0 || localMatrix.m23() != 0) {
            localMatrix.m03(0).m13(0).m23(0).m33(1);
        }

        translation.set(localMatrix.m30(), localMatrix.m31(), localMatrix.m32());
        localMatrix.m30(0).m31(0).m32(0).m33(localMatrix.m33());

        Vector3f[] row = new Vector3f[3];

        for (int i = 0; i < 3; ++i) {
            row[i] = new Vector3f();
            for (int j = 0; j < 3; ++j)
                row[i].setComponent(j, localMatrix.get(i, j));
        }

        scale.x = row[0].length();
        row[0].normalize();
        scale.y = row[1].length();
        row[1].normalize();
        scale.z = row[2].length();
        row[2].normalize();

        rotation.y = (float) Math.asin(-row[0].z);
        if (Math.cos(rotation.y) != 0) {
            rotation.x = (float) Math.atan2(row[1].z, row[2].z);
            rotation.z = (float) Math.atan2(row[0].y, row[0].x);
        } else {
            rotation.x = (float) Math.atan2(-row[2].x, row[1].y);
            rotation.z = 0;
        }

        return true;
    }
}
