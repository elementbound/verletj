package com.github.elementbound.verletj.scene;

import com.github.elementbound.verletj.simulation.CircleEntity;
import com.github.elementbound.verletj.simulation.Simulator;
import com.github.elementbound.verletj.simulation.constraint.GlobalDistanceConstraint;
import com.github.elementbound.verletj.simulation.effector.GravityEffector;
import com.github.elementbound.verletj.simulation.effector.SoftBodyVertexEffector;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Random;

public class SoftBodyScene extends AsyncScene {
    @Override
    protected void asyncRun(Simulator simulator) {
        var random = new Random();
        random.setSeed(0xC0FFEE);

        var distanceConstraint = new GlobalDistanceConstraint(simulator.getCircles());
        distanceConstraint.setMaxDistance(7.0);
        simulator.addConstraint(distanceConstraint);

        var gravityEffector = new GravityEffector(new Vector2d(0.0, -0.5), simulator.getCircles());
        simulator.addEffector(gravityEffector);

        createCircle(simulator, new Vector2d(0., 0.), 2., 0.125);
    }

    private void createCircle(Simulator simulator, Vector2d position, double r, double thickness) {
        var circumference = 2 * Math.PI * r;
        createCircle(simulator, position, r, thickness, (int) (circumference / (2.05 * thickness)));
    }

    private void createCircle(Simulator simulator, Vector2d position, double r, double thickness, int res) {
        var circles = new ArrayList<CircleEntity>();

        for (var i = 0; i < res; i++) {
            var a = i / (double) (res) * 2.0 * Math.PI;
            var x = Math.cos(a) * r;
            var y = Math.sin(a) * r;

            var circle = new CircleEntity();
            circle.setR(thickness);
            circle.setPosition(position);
            circle.getPosition().add(x, y);
            circle.resetVelocity();

            circles.add(circle);
            simulator.spawn(circle);
        }

        System.out.printf("Spawned %d circles\n", circles.size());

        for (var i = 0; i < circles.size(); ++i) {
            var leftIdx = (i + circles.size() - 1) % circles.size();
            var rightIdx = (i + 1) % circles.size();

            var left = circles.get(leftIdx);
            var middle = circles.get(i);
            var right = circles.get(rightIdx);

            var effector = new SoftBodyVertexEffector(middle);
            effector.link(left);
            effector.link(right);

            simulator.addEffector(effector);
        }
    }
}
