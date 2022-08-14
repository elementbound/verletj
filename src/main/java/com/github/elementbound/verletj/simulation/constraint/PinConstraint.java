package com.github.elementbound.verletj.simulation.constraint;

import com.github.elementbound.verletj.GLUtils;
import com.github.elementbound.verletj.simulation.SphereEntity;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class PinConstraint implements Constraint {
    private final Vector3d COLOR = new Vector3d(1., 0., 0.);

    private final Vector2d pinPosition;
    private final SphereEntity entity;

    public PinConstraint(SphereEntity entity) {
        this.entity = entity;
        this.pinPosition = new Vector2d(entity.getPosition());
    }

    @Override
    public void apply() {
        entity.setPosition(pinPosition);
    }

    @Override
    public void draw() {
        GLUtils.drawCircle(pinPosition, 0.25, COLOR, false);
    }
}
