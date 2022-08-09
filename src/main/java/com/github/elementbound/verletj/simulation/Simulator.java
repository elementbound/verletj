package com.github.elementbound.verletj.simulation;

import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private static final Vector2d GRAVITY = new Vector2d(0.0, -2.0);

    private List<SphereEntity> spheres = new ArrayList<>();

    public void spawn(SphereEntity entity) {
        spheres.add(entity);
        entity.onSpawn();
    }

    public void simulate(double t, double dt) {
        // Gravity
        spheres.forEach(e -> e.accelerate(GRAVITY));

        // Simulate entities
        spheres.forEach(e -> e.simulate(t, dt));

        final var substepping = 8;

        for (int s = 0; s < substepping; ++s) {
            // Constrain distance from origin
            spheres.forEach(e -> {
                var position = e.getPosition();
                if (position.length() > 8.0 - e.getR()) {
                    position.normalize(8.0 - e.getR());
                }
            });

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

                // System.out.printf("rA=%f; rB=%f; d=%f\n", a.getR(), b.getR(), a.getPosition().distance(b.getPosition()));
            });
        }
    }

    public void draw() {
        spheres.forEach(Entity::draw);
    }

    private static record Collision(SphereEntity a, SphereEntity b,
                                    double distance) {
    }
}
