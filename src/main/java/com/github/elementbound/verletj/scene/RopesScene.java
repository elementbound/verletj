package com.github.elementbound.verletj.scene;

import com.github.elementbound.verletj.MathUtil;
import com.github.elementbound.verletj.simulation.CircleEntity;
import com.github.elementbound.verletj.simulation.Simulator;
import com.github.elementbound.verletj.simulation.constraint.GlobalDistanceConstraint;
import com.github.elementbound.verletj.simulation.constraint.LinkConstraint;
import com.github.elementbound.verletj.simulation.constraint.PinConstraint;
import com.github.elementbound.verletj.simulation.effector.GravityEffector;
import org.joml.Vector2d;

import java.util.Random;

public class RopesScene extends AsyncScene {
    @Override
    protected void asyncRun(Simulator simulator) {
        var random = new Random();

        var distanceConstraint = new GlobalDistanceConstraint(simulator.getCircles());
        distanceConstraint.setMaxDistance(7.0);
        simulator.addConstraint(distanceConstraint);

        var gravityEffector = new GravityEffector(new Vector2d(0.0, -0.5), simulator.getCircles());
        simulator.addEffector(gravityEffector);

        long restMillis = 100;
        int ropes = random.nextInt(16, 24);
        for (int i = 0; i < ropes; ++i) {
            var length = random.nextDouble(1.0, 4.0);
            var thickness = 0.125;

            var axis = MathUtil.directionVector(random.nextDouble() * 2.0 * Math.PI);
            var origin = MathUtil.directionVector(
                    random.nextDouble() * Math.PI,
                    distanceConstraint.getMaxDistance() - thickness
            );

            var pinNode = new CircleEntity();
            pinNode.setPosition(origin);
            pinNode.resetVelocity();
            pinNode.setR(thickness);

            var pinConstraint = new PinConstraint(pinNode);

            simulator.spawn(pinNode);
            simulator.addConstraint(pinConstraint);

            var previousNode = pinNode;
            for (double f = thickness; f < length; f += thickness) {
                var linkNode = new CircleEntity();
                linkNode.getPosition()
                        .set(axis)
                        .mul(thickness)
                        .add(previousNode.getPosition());
                linkNode.resetVelocity();
                linkNode.setR(thickness);

                var linkConstraint = new LinkConstraint(previousNode, linkNode);

                simulator.spawn(linkNode);
                simulator.addConstraint(linkConstraint);

                previousNode = linkNode;

                sleep(restMillis);
            }
        }
    }
}
