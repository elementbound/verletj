package com.github.elementbound.verletj.scene;

import com.github.elementbound.verletj.MathUtil;
import com.github.elementbound.verletj.simulation.CircleEntity;
import com.github.elementbound.verletj.simulation.Simulator;
import com.github.elementbound.verletj.simulation.constraint.GlobalDistanceConstraint;
import com.github.elementbound.verletj.simulation.effector.GravityEffector;
import org.joml.Vector2d;

import java.util.Random;

public class CirclesScene extends AsyncScene {
    @Override
    protected void asyncRun(Simulator simulator) {
        var random = new Random();
        random.setSeed(0xC0FFEE);

        var distanceConstraint = new GlobalDistanceConstraint(simulator.getCircles());
        distanceConstraint.setMaxDistance(7.0);
        simulator.addConstraint(distanceConstraint);

        var gravityEffector = new GravityEffector(new Vector2d(0.0, -0.5), simulator.getCircles());
        simulator.addEffector(gravityEffector);

        int count = 512;
        long restMillis = 10;
        for (int i = 0; i < count; ++i) {
            var circle = new CircleEntity();
            circle.getPosition().x = MathUtil.birandom(random, 3.0);
            circle.getPosition().y = MathUtil.birandom(random, 3.0);
            circle.resetVelocity();

            circle.setR(MathUtil.randomBetween(random, 0.125, 0.5) / 2.0);

            simulator.spawn(circle);

            sleep(restMillis);
        }
    }
}
