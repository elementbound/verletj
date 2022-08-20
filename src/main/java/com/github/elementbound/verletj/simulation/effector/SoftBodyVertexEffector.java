package com.github.elementbound.verletj.simulation.effector;

import com.github.elementbound.verletj.GLUtils;
import com.github.elementbound.verletj.simulation.CircleEntity;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class SoftBodyVertexEffector implements Effector {
    private final CircleEntity targetEntity;
    private final List<CircleEntity> linkEntities = new ArrayList<>();
    private final List<Vector2d> linkOffsets = new ArrayList<>();

    private double strength = 16.;
    private double range = 1.;
    private double restRange = 0;
    private double falloff = 1.;

    private Vector2d targetPosition = new Vector2d();
    private Vector2d accelaration = new Vector2d();

    public SoftBodyVertexEffector(CircleEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public void link(CircleEntity entity) {
        linkEntities.add(entity);

        var offset = new Vector2d(entity.getPosition())
                .sub(targetEntity.getPosition());
        linkOffsets.add(offset);
    }

    @Override
    public void apply() {
        targetPosition.zero();

        for (int i = 0; i < linkEntities.size(); ++i) {
            var linkEntity = linkEntities.get(i);
            var linkOffset = linkOffsets.get(i);

            targetPosition.add(linkEntity.getPosition())
                    .sub(linkOffset);
        }

        targetPosition.div(linkEntities.size());

        var offset = new Vector2d(targetPosition)
                .sub(targetEntity.getPosition());
        var f = offset.length() > 0.
                ? (strength * Math.pow(Math.max(offset.length() - restRange, 0.0) / (range - restRange), falloff))
                : 0.;
        offset.normalize(f);

        if (offset.isFinite()) {
            accelaration.set(offset);
            targetEntity.accelerate(offset);
        } else {
            accelaration.zero();
        }
    }

    @Override
    public void draw() {
        linkEntities.forEach(e -> {
            GLUtils.drawLine(targetEntity.getPosition(), e.getPosition(), GLUtils.GREEN);
        });

        GLUtils.drawCircle(targetPosition, 0.25, GLUtils.RED, false);
        GLUtils.drawLine(targetEntity.getPosition().x, targetEntity.getPosition().y,
                targetEntity.getPosition().x + accelaration.x, targetEntity.getPosition().y + accelaration.y,
                1., 0., 0.);
    }
}
