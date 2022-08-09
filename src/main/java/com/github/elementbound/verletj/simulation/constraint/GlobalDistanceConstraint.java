package com.github.elementbound.verletj.simulation.constraint;

import com.github.elementbound.verletj.simulation.SphereEntity;
import org.joml.Vector2d;

import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;

public class GlobalDistanceConstraint implements Constraint {
    private final List<SphereEntity> spheres;
    private final Vector2d origin = new Vector2d(0.0);
    private double maxDistance = 1.0;

    public GlobalDistanceConstraint(List<SphereEntity> spheres) {
        this.spheres = spheres;
    }

    @Override
    public void apply() {
        for (var sphere : spheres) {
            var offset = new Vector2d(sphere.getPosition()).sub(origin);

            if (offset.length() + sphere.getR() > maxDistance) {
                offset.normalize(maxDistance - sphere.getR());
                sphere.getPosition().set(origin).add(offset);
            }
        }
    }

    @Override
    public void draw() {
        glBegin(GL_LINE_LOOP);

        final var res = 64;
        for (int i = 0; i < res; ++i) {
            double f = i / (double) res;
            double a = f * 2.0 * Math.PI;

            glVertex2d(Math.cos(a) * this.maxDistance, Math.sin(a) * this.maxDistance);
        }

        glEnd();
    }

    public Vector2d getOrigin() {
        return origin;
    }

    public void setOrigin(Vector2d origin) {
        this.origin.set(origin);
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GlobalDistanceConstraint that = (GlobalDistanceConstraint) o;
        return Double.compare(that.maxDistance, maxDistance) == 0 && origin.equals(that.origin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, maxDistance);
    }
}
