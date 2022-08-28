package com.github.elementbound.verletj.simulation.effector;

import com.github.elementbound.verletj.GLUtils;
import com.github.elementbound.verletj.simulation.CircleEntity;
import org.joml.Vector2d;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class SoftBodyEffector implements Effector {
    private final Set<CircleEntity> entities = new HashSet<>();
    private final Map<CircleEntity, Vector2d> entityOffsets = new IdentityHashMap<>();
    private final Vector2d midpoint = new Vector2d();
    private final Vector2d boundsMin = new Vector2d();
    private final Vector2d boundsMax = new Vector2d();

    private double strength = 64.;
    private double range = 2.;
    private double falloff = 2.;

    public void add(CircleEntity entity) {
        entities.add(entity);
    }

    @Override
    public void apply() {
        updateMidpoint();
        ensureOffsets();

        for (var entity : entities) {
            // TODO: Allocate temp vars on stack
            var offset = entityOffsets.get(entity);
            var target = new Vector2d(midpoint).add(offset);

            var delta = new Vector2d(target).sub(entity.getPosition());
            var distance = delta.length();
            var f = distance > 0.
                    ? (strength * Math.pow(distance / range, falloff))
                    : 0.;

            delta.normalize(f);
            if (delta.isFinite()) {
                entity.accelerate(delta);
            }
        }
    }

    @Override
    public void postResolve() {
        updateMidpoint();
    }

    @Override
    public void draw() {
        GLUtils.drawCircle(midpoint, 0.5, GLUtils.GREEN, false);

        for (var entity : entities) {
            // TODO: Allocate temp vars on stack
            var offset = entityOffsets.get(entity);
            if (offset == null) {
                continue;
            }

            var target = new Vector2d(midpoint).add(offset);

            // GLUtils.drawCircle(target, 0.125, GLUtils.BLUE, false);
            // GLUtils.drawLine(entity.getPosition(), target, GLUtils.BLUE);

            var delta = new Vector2d(target).sub(entity.getPosition());
            var distance = delta.length();
            var f = distance > 0.
                    ? (strength * Math.pow(distance / range, falloff))
                    : 0.;
            delta.normalize(f).add(entity.getPosition());
            GLUtils.drawLine(entity.getPosition(), delta, GLUtils.BLUE);
        }

        GLUtils.drawLine(boundsMin.x, boundsMin.y, boundsMax.x, boundsMin.y, 1., 1., 0.);
        GLUtils.drawLine(boundsMax.x, boundsMin.y, boundsMax.x, boundsMax.y, 1., 1., 0.);
        GLUtils.drawLine(boundsMin.x, boundsMax.y, boundsMax.x, boundsMax.y, 1., 1., 0.);
        GLUtils.drawLine(boundsMin.x, boundsMin.y, boundsMin.x, boundsMax.y, 1., 1., 0.);
    }

    private void updateMidpoint() {
        midpoint.zero();

        if (entities.isEmpty()) {
            return;
        }

        boundsMin.set(Double.POSITIVE_INFINITY);
        boundsMax.set(Double.NEGATIVE_INFINITY);

        entities.forEach(entity -> {
            boundsMin.min(entity.getPosition());
            boundsMax.max(entity.getPosition());
        });

        midpoint.add(boundsMin)
                .add(boundsMax)
                .div(2.0);
    }

    private void ensureOffsets() {
        entities.stream()
                .filter(entity -> !entityOffsets.containsKey(entity))
                .forEach(entity -> {
                    Vector2d offset = new Vector2d();
                    offset.set(entity.getPosition())
                            .sub(midpoint);

                    entityOffsets.put(entity, offset);
                });
    }
}
