package com.github.elementbound.verletj;

import org.joml.Vector2d;

import java.util.Random;

public class MathUtil {
    public static double birandom(Random random) {
        return 1.0 - 2.0 * random.nextDouble();
    }

    public static double birandom(Random random, double extent) {
        return birandom(random) * extent;
    }

    public static double lerp(double a, double b, double t) {
        return (1.0 - t) * a + t * b;
    }

    public static double randomBetween(Random random, double a, double b) {
        return lerp(a, b, random.nextDouble());
    }

    public static Vector2d directionVector(double direction, double length) {
        return new Vector2d(Math.cos(direction) * length, Math.sin(direction) * length);
    }

    public static Vector2d directionVector(double direction) {
        return directionVector(direction, 1.0);
    }
}
