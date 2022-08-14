package com.github.elementbound.verletj.simulation;

import com.github.elementbound.verletj.GLUtils;
import org.joml.Vector2d;

import java.util.Objects;
import java.util.StringJoiner;

public class SphereEntity implements Entity {
    private double r = 1.0;
    private final Vector2d position = new Vector2d();
    private final Vector2d previousPosition = new Vector2d();
    private final Vector2d acceleration = new Vector2d();

    public void accelerate(Vector2d acceleration) {
        this.acceleration.add(acceleration);
    }

    @Override
    public void simulate(double t, double dt) {
        // v = position - previousPosition
        var velocity = new Vector2d(position).sub(previousPosition);
        acceleration.mul(dt);

        previousPosition.set(position);
        position.add(velocity);
        position.add(acceleration);

        acceleration.set(0.0);
    }

    @Override
    public void draw() {
        GLUtils.drawCircle(position, r, true);
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector2d position) {
        this.position.set(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SphereEntity that = (SphereEntity) o;
        return Double.compare(that.r, r) == 0 && position.equals(that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, position);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SphereEntity.class.getSimpleName() + "[", "]")
                .add("r=" + r)
                .add("position=" + position)
                .add("previousPosition=" + previousPosition)
                .toString();
    }
}
