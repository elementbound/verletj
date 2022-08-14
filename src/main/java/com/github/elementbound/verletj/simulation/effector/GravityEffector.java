package com.github.elementbound.verletj.simulation.effector;

import com.github.elementbound.verletj.simulation.SphereEntity;
import org.joml.Vector2d;

import java.util.List;

public class GravityEffector implements Effector {
    private final Vector2d gravity = new Vector2d();
    private final List<SphereEntity> spheres;

    public GravityEffector(Vector2d gravity, List<SphereEntity> spheres) {
        this.spheres = spheres;
        this.gravity.set(gravity);
    }

    @Override
    public void apply() {
        spheres.forEach(s -> s.accelerate(gravity));
    }
}
