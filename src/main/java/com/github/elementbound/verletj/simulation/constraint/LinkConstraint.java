package com.github.elementbound.verletj.simulation.constraint;

import com.github.elementbound.verletj.simulation.CircleEntity;
import org.joml.Vector2d;

import java.util.Objects;
import java.util.StringJoiner;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;

public class LinkConstraint implements Constraint {
    private final CircleEntity a;
    private final CircleEntity b;
    private final double distance;

    public LinkConstraint(CircleEntity a, CircleEntity b) {
        this.a = a;
        this.b = b;
        distance = a.getPosition().distance(b.getPosition());
    }

    @Override
    public void apply() {
        var origin = new Vector2d(a.getPosition()).add(b.getPosition()).div(2.0);
        var offset = new Vector2d(a.getPosition()).sub(origin).normalize(distance);

        a.getPosition().set(origin).add(offset);
        b.getPosition().set(origin).sub(offset);
    }

    @Override
    public void draw() {
        glBegin(GL_LINES);

        glVertex2d(a.getPosition().x(), a.getPosition().y());
        glVertex2d(b.getPosition().x(), b.getPosition().y());

        glEnd();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LinkConstraint that = (LinkConstraint) o;
        return a.equals(that.a) && b.equals(that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LinkConstraint.class.getSimpleName() + "[", "]")
                .add("a=" + a)
                .add("b=" + b)
                .add("distance=" + distance)
                .toString();
    }
}
