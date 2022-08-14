package com.github.elementbound.verletj.simulation.effector;

import com.github.elementbound.verletj.simulation.CircleEntity;
import org.joml.Vector2d;

import java.util.List;

public class GravityEffector implements Effector {
    private final Vector2d gravity = new Vector2d();
    private final List<CircleEntity> circles;

    public GravityEffector(Vector2d gravity, List<CircleEntity> circles) {
        this.circles = circles;
        this.gravity.set(gravity);
    }

    @Override
    public void apply() {
        circles.forEach(s -> s.accelerate(gravity));
    }
}
