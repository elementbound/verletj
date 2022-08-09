package com.github.elementbound.verletj.simulation;

import com.github.elementbound.verletj.simulation.constraint.Constraint;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulator {
    private static final Vector2d GRAVITY = new Vector2d(0.0, -2.0);

    private final List<SphereEntity> spheres = new ArrayList<>();
    private final List<SphereEntity> immutableSpheresView = Collections.unmodifiableList(spheres);

    private final List<Constraint> constraints = new ArrayList<>();

    public void spawn(SphereEntity entity) {
        spheres.add(entity);
        entity.onSpawn();
    }

    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }

    public void simulate(double t, double dt) {
        // Gravity
        spheres.forEach(e -> e.accelerate(GRAVITY));

        // Simulate entities
        spheres.forEach(e -> e.simulate(t, dt));

        final var substepping = 16;

        for (int s = 0; s < substepping; ++s) {
            // Apply constraints
            constraints.forEach(Constraint::apply);

            // Find collisions
            List<Collision> collisions = new ArrayList<>();

            for (int i = 0; i < spheres.size(); ++i) {
                for (int j = i + 1; j < spheres.size(); ++j) {
                    var sphereA = spheres.get(i);
                    var sphereB = spheres.get(j);

                    var posA = sphereA.getPosition();
                    var posB = sphereB.getPosition();

                    var rA = sphereA.getR();
                    var rB = sphereB.getR();

                    var distance = posA.distance(posB);

                    if (distance < rA + rB) {
                        collisions.add(new Collision(sphereA, sphereB, distance));
                    }
                }
            }

            // Resolve collisions
            collisions.forEach(collision -> {
                var a = collision.a();
                var b = collision.b();

                var axis = new Vector2d(b.getPosition())
                        .sub(a.getPosition())
                        .normalize(a.getR() + b.getR() - collision.distance())
                        .div(2.0);

                a.getPosition().sub(axis);
                b.getPosition().add(axis);
            });
        }
    }

    public void draw() {
        spheres.forEach(Entity::draw);
        constraints.forEach(Constraint::draw);
    }

    public List<SphereEntity> getSpheres() {
        return immutableSpheresView;
    }

    private static record Collision(SphereEntity a, SphereEntity b,
                                    double distance) {
    }
}
