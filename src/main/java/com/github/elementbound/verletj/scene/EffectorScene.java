package com.github.elementbound.verletj.scene;

import com.github.elementbound.verletj.MathUtil;
import com.github.elementbound.verletj.simulation.CircleEntity;
import com.github.elementbound.verletj.simulation.Simulator;
import com.github.elementbound.verletj.simulation.constraint.GlobalDistanceConstraint;
import com.github.elementbound.verletj.simulation.effector.ForceEffector;
import com.github.elementbound.verletj.simulation.effector.GravityEffector;
import org.joml.Vector2d;

import java.util.Random;

public class EffectorScene extends AsyncScene {
    @Override
    protected void asyncRun(Simulator simulator) {
        var random = new Random();
        random.setSeed(0xC0FFEE);

        var distanceConstraint = new GlobalDistanceConstraint(simulator.getCircles());
        distanceConstraint.setMaxDistance(7.0);
        simulator.addConstraint(distanceConstraint);

        var gravityEffector = new GravityEffector(new Vector2d(0.0, -0.5), simulator.getCircles());
        simulator.addEffector(gravityEffector);

        var forceEffector = new ForceEffector(simulator.getCircles());
        forceEffector.setStrength(-4.0);
        forceEffector.setRange(4.0);
        forceEffector.setFalloff(2.0);
        forceEffector.getPosition().set(0., distanceConstraint.getMaxDistance() - forceEffector.getRange());
        simulator.addEffector(forceEffector);

        int count = 768;
        long restMillis = 5;
        for (int i = 0; i < count; ++i) {
            var circle = new CircleEntity();
            circle.getPosition().x = MathUtil.birandom(random, 3.0);
            circle.getPosition().y = MathUtil.birandom(random, 3.0);
            circle.resetVelocity();

            circle.setR(0.25);

            simulator.spawn(circle);

            sleep(restMillis);
        }
    }
}
