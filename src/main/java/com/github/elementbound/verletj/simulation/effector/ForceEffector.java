package com.github.elementbound.verletj.simulation.effector;

import com.github.elementbound.verletj.GLUtils;
import com.github.elementbound.verletj.simulation.CircleEntity;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.List;

public class ForceEffector implements Effector {
    private static final Vector3d BORDER_COLOR = new Vector3d(0.5, 0.5, 0.75);
    private static final Vector3d INNER_COLOR = new Vector3d(0.25, 0.25, 0.375);

    private final Vector2d position = new Vector2d();
    private final List<CircleEntity> circles;
    private double range;
    private double radialStrength;
    private double vortexStrength;
    private double falloff;

    public ForceEffector(List<CircleEntity> circles) {
        this.circles = circles;
    }

    @Override
    public void apply() {
        var delta = new Vector2d();

        for (var circle : circles) {
            delta.set(circle.getPosition())
                    .sub(position);

            var distance = delta.length();

            if (distance > range) {
                continue;
            }

            var f = Math.pow(1.0 - distance / range, falloff);


            var tangent = new Vector2d(delta).normalize(f * vortexStrength);
            double t = tangent.x;
            tangent.x = -tangent.y;
            tangent.y = t;

            delta.normalize(f * radialStrength);
            circle.accelerate(delta);
            circle.accelerate(tangent);
        }
    }

    @Override
    public void draw() {
        if (falloff != 0.0) {
            final int innerCircles = 8;
            for (int i = 1; i < innerCircles; ++i) {
                var f = i / (double) innerCircles;

                // Calculate the distance at which f = i / innerCircles
                var d = range * (1.0 - Math.pow(f, 1.0 / falloff));
                GLUtils.drawCircle(position, d, INNER_COLOR, false, 32);
            }
        }

        GLUtils.drawCircle(position, range, BORDER_COLOR, false, 32);
    }

    public Vector2d getPosition() {
        return position;
    }

    public void setPosition(Vector2d position) {
        this.position.set(position);
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getRadialStrength() {
        return radialStrength;
    }

    public void setRadialStrength(double radialStrength) {
        this.radialStrength = radialStrength;
    }

    public double getVortexStrength() {
        return vortexStrength;
    }

    public void setVortexStrength(double vortexStrength) {
        this.vortexStrength = vortexStrength;
    }

    public double getFalloff() {
        return falloff;
    }

    public void setFalloff(double falloff) {
        this.falloff = falloff;
    }

    public List<CircleEntity> getCircles() {
        return circles;
    }
}
