package com.github.elementbound.verletj.simulation;

import com.github.elementbound.verletj.simulation.constraint.Constraint;
import com.github.elementbound.verletj.simulation.effector.Effector;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulator {
    private final List<CircleEntity> circles = new ArrayList<>();
    private final List<CircleEntity> immutableCirclesView = Collections.unmodifiableList(circles);

    private final List<Constraint> constraints = new ArrayList<>();
    private final List<Effector> effectors = new ArrayList<>();

    public void spawn(CircleEntity entity) {
        circles.add(entity);
    }

    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }

    public void addEffector(Effector effector) {
        effectors.add(effector);
    }

    public void simulate(double t, double dt) {
        // Effectors
        effectors.forEach(Effector::apply);

        // Simulate entities
        circles.forEach(e -> e.simulate(t, dt));

        final var substepping = 16;

        for (int s = 0; s < substepping; ++s) {
            // Apply constraints
            constraints.forEach(Constraint::apply);

            // Find collisions
            List<Collision> collisions = new ArrayList<>();

            for (int i = 0; i < circles.size(); ++i) {
                for (int j = i + 1; j < circles.size(); ++j) {
                    var circleA = circles.get(i);
                    var circleB = circles.get(j);

                    var posA = circleA.getPosition();
                    var posB = circleB.getPosition();

                    var rA = circleA.getR();
                    var rB = circleB.getR();

                    var distance = posA.distance(posB);

                    if (distance < rA + rB) {
                        collisions.add(new Collision(circleA, circleB, distance));
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
        circles.forEach(CircleEntity::draw);
        constraints.forEach(Constraint::draw);
        effectors.forEach(Effector::draw);
    }

    public List<CircleEntity> getCircles() {
        return immutableCirclesView;
    }

    private static record Collision(CircleEntity a, CircleEntity b,
                                    double distance) {
    }
}
