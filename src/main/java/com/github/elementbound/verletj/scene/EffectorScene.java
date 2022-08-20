package com.github.elementbound.verletj.scene;

import com.github.elementbound.verletj.MathUtil;
import com.github.elementbound.verletj.simulation.CircleEntity;
import com.github.elementbound.verletj.simulation.Simulator;
import com.github.elementbound.verletj.simulation.constraint.GlobalDistanceConstraint;
import com.github.elementbound.verletj.simulation.effector.ForceEffector;

import java.util.Random;

public class EffectorScene extends AsyncScene {
    @Override
    protected void asyncRun(Simulator simulator) {
        var random = new Random();
        random.setSeed(0xC0FFEE);

        var distanceConstraint = new GlobalDistanceConstraint(simulator.getCircles());
        distanceConstraint.setMaxDistance(7.0);
        simulator.addConstraint(distanceConstraint);

        var topEffector = new ForceEffector(simulator.getCircles());
        topEffector.setRadialStrength(-4.0);
        topEffector.setVortexStrength(4.0);
        topEffector.setRange(4.0);
        topEffector.setFalloff(2.0);
        topEffector.getPosition().set(0., distanceConstraint.getMaxDistance() - topEffector.getRange());
        simulator.addEffector(topEffector);

        var bottomEffector = new ForceEffector(simulator.getCircles());
        bottomEffector.setRadialStrength(-4.0);
        bottomEffector.setVortexStrength(-4.0);
        bottomEffector.setRange(4.0);
        bottomEffector.setFalloff(2.0);
        bottomEffector.getPosition().set(topEffector.getPosition()).negate();
        simulator.addEffector(bottomEffector);

        int count = 512;
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
